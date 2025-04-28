# =============================================================
#  Mini-Chatbot Backend (FastAPI)
# -------------------------------------------------------------
#   POST /ask_mini - 미니 챗봇 단독 질문 응답
# =============================================================

import asyncio
import os
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from openai import OpenAI
from pydantic import BaseModel
from dotenv import load_dotenv
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates
from fastapi import Request
from typing import List

# 실행 위치: cd fastAPI\python-api
# 실행 명령어: uvicorn api_server2:app --reload --port 8000

# 실행 위치: cd fastAPI\python-api
# 실행 명령어: uvicorn api_server2:app --reload --port 8000

# ------------------------------------------------------------------
# 0. 환경 변수 로드 & Assistant ID
# ------------------------------------------------------------------
load_dotenv()  # ✅ .env 한 번만 로드

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

assistant_template_id = os.getenv("ASSISTANT_TEMPLATE_ID")
assistant_feedback_id = os.getenv("ASSISTANT_REVIEW_ID")
assistant_mini_id = os.getenv("ASSISTANT_MINI_ID")


# # ✅ 디버깅 출력
# print(f"템플릿 Assistant ID: {assistant_template_id}")
# print(f"피드백 Assistant ID: {assistant_feedback_id}")

# ------------------------------------------------------------------
# 1. FastAPI 인스턴스 & CORS 설정
# ------------------------------------------------------------------
app = FastAPI(title="Mini-Chatbot Backend", version="1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8087"],  # 프론트엔드 주소
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ------------------------------------------------------------------
# 2. 데이터 모델
# ------------------------------------------------------------------
class ChatRequest(BaseModel):
    message: str
    thread_id: str
    # assistant_id: str | None = None  # 없으면 템플릿용 기본 assistant 사용

# ------------------------------------------------------------------
# 3. GPT 호출 함수
# ------------------------------------------------------------------
async def call_mini_assistant(
    thread_id:str,
    content: str,
    assistant_id: str,
    timeout: int = 30,
) -> str:
    thread = thread_id
    client.beta.threads.messages.create(thread_id=thread_id, role="user", content=content)
    run = client.beta.threads.runs.create(thread_id=thread_id, assistant_id=assistant_id)

    elapsed = 0.0
    while True:
        result = client.beta.threads.runs.retrieve(thread_id=thread_id, run_id=run.id)
        if result.status == "completed":
            break
        await asyncio.sleep(0.5)
        elapsed += 0.5
        if elapsed > timeout:
            raise HTTPException(504, "Assistant 응답 지연")

    messages = client.beta.threads.messages.list(thread_id=thread_id, order="desc")
    return messages.data[0].content[0].text.value

# ------------------------------------------------------------------
# 4. 엔드포인트
# ------------------------------------------------------------------
@app.post("/ask_mini")
async def ask_mini(req: ChatRequest):
    assistant_id = assistant_mini_id
    ans = await call_mini_assistant(req.thread_id, req.message, assistant_id)
    return {"answer": ans}

templates = Jinja2Templates(directory="templates")  

@app.get("/index", response_class=HTMLResponse)
async def index(request: ChatRequest):
    return templates.TemplateResponse(
        "index.html", 
        {"request": request, 
         "thread_id": request.thread_id,
         "template_assistant_id": assistant_template_id, 
         "feedback_assistant_id": assistant_feedback_id}
    )
# ------------------------------------------------------------------
# 5. 이전 대화 기록 불러오기 -----------------------------------------
# ------------------------------------------------------------------


class PreviousChatRecord(BaseModel):
    chatter: str  # "user" 또는 "bot"
    chat_content: str  # 텍스트 내용

@app.get("/get_previous_chat", response_model=List[PreviousChatRecord])
async def get_previous_chat(thread_id: str):
    """
    주어진 thread_id로 OpenAI API에 요청하여
    과거 대화 메시지 전체를 가져온다.
    """
    try:
        # OpenAI 쓰레드에서 전체 메시지 가져오기
        messages = client.beta.threads.messages.list(thread_id=thread_id, order="asc")
        result = []
        for msg in messages.data:
            role = msg.role
            # role이 'assistant'이면 chatter="bot", 아니면 "user"
            chatter = "bot" if role == "assistant" else "user"
            content = msg.content[0].text.value if msg.content else ""
            result.append(PreviousChatRecord(chatter=chatter, chat_content=content))

        return result

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching previous chat: {str(e)}")