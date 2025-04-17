import time
from flask import Flask, request, jsonify
from flask_cors import CORS
from openai import OpenAI
from dotenv import load_dotenv
import os

# 환경 변수 불러오기
load_dotenv()
client = OpenAI(api_key=os.getenv("openai_api_key"))

app = Flask(__name__)
CORS(app)

thread_id = None
thread_file = "thread_id.txt"

# 서버 시작 시 thread_id 불러오기 또는 생성
if os.path.exists(thread_file):
    with open(thread_file, "r") as f:
        thread_id = f.read().strip()
else:
    thread = client.beta.threads.create()
    thread_id = thread.id
    with open(thread_file, "w") as f:
        f.write(thread_id)

# ✅ 1. 메인 대화 처리 API
@app.route("/ask", methods=["POST"])
def ask():
    global thread_id
    data = request.get_json()
    prompt = data["message"]

    client.beta.threads.messages.create(
        thread_id=thread_id,
        role="user",
        content=prompt
    )

    run = client.beta.threads.runs.create(
        thread_id=thread_id,
        assistant_id="asst_Q6wIGhBkWz9bk5GqhQqjdHJb"
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

    return jsonify({"answer": answer})

# ✅ 2. 대화 전체 기록 조회 API
@app.route("/get_history", methods=["GET"])
def get_history():
    messages = client.beta.threads.messages.list(thread_id=thread_id, order="asc")
    conversation = []
    for msg in messages.data:
        content = msg.content[0].text.value.strip()
        speaker = "🤖 챗봇" if msg.role == "assistant" else "🙂 사용자"
        conversation.append(f"{speaker}: {content}")
    return jsonify({"history": "\n\n".join(conversation)})

# 서버 실행
if __name__ == "__main__":
    app.run(debug=True)
