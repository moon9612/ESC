import time
import os
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from openai import OpenAI
from dotenv import load_dotenv

# 실행코드 : uvicorn api_server:app --reload --port 8000
# 환경 변수 로드
load_dotenv()
client = OpenAI(api_key=os.getenv("openai_api_key"))
assistant_id = os.getenv("assistant_id")
app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8087"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 쓰레드 ID 로딩 또는 생성
# thread_file = "thread_id.txt"
# if os.path.exists(thread_file):
#     with open(thread_file, "r") as f:
#         thread_id = f.read().strip()
# else:
#     thread = client.beta.threads.create()
#     thread_id = thread.id
#     with open(thread_file, "w") as f:
#         f.write(thread_id)

# thread_id 생성
thread = client.beta.threads.create()
thread_id = thread.id


# 메시지 요청 모델
class MessageRequest(BaseModel):
    message: str

# @app.get("/ask")
# def read_root():
#     return {"message": "Hello World!"}


# ✅ 1. 대화 처리 엔드포인트
@app.post("/ask")
async def ask(request: MessageRequest):
    global thread_id

    client.beta.threads.messages.create(
        thread_id=thread_id,
        role="user",
        content=request.message
    )

    run = client.beta.threads.runs.create(
        thread_id=thread_id,
        assistant_id=assistant_id
    )

    while True:
        result = client.beta.threads.runs.retrieve(
            thread_id=thread_id,
            run_id=run.id
        )
        if result.status == "completed":
            break
        time.sleep(0.3)

    response = client.beta.threads.messages.list(thread_id=thread_id)
    answer = response.data[0].content[0].text.value

    return {"answer": answer}

# ✅ 2. 대화 기록 조회
@app.get("/get_history")
async def get_history():
    global thread_id
    messages = client.beta.threads.messages.list(thread_id=thread_id, order="asc")
    conversation = []
    for msg in messages.data:
        content = msg.content[0].text.value.strip()
        speaker = "🤖 챗봇" if msg.role == "assistant" else "🙂 사용자"
        conversation.append(f"{speaker}: {content}")
    return {"history": "\n\n".join(conversation)}
