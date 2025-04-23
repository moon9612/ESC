# =============================================================
#  Petition-AI Backend (FastAPI)
# -------------------------------------------------------------
# • assistant_go_id        : 고용노동 일반 상담 Assistant
# • assistant_san_id       : 산업재해(산재) 상담 Assistant
# • assistant_review_id    : 항목 검토 Assistant  (JSON 피드백)
# • assistant_template_id  : 진정 내용 자동 작성 Assistant
#
#  주요 REST 엔드포인트
#   POST /create_thread     - 신규 thread_id 발급
#   POST /ask_go            - 고용노동 Q&A
#   POST /ask_san           - 산재 Q&A
#   POST /review_input      - 항목 검토(JSON)
#   POST /apply_feedback    - 검토 결과 수락(병합)
#   POST /auto_template     - 본문 초안 생성
#   POST /generate_document - 최종 문서 JSON 반환
# -------------------------------------------------------------
#  수정 이력
#   2025-04-23: async polling, context_lock, 로깅, 주석 보강
# =============================================================

import asyncio
import json
import logging
import os
from typing import Dict

from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from openai import OpenAI
from pydantic import BaseModel, Field, field_validator
from dotenv import load_dotenv

# 실행 위치: cd fastAPI\python-api
# 실행 명령어: uvicorn api_server:app --reload --port 8000

# ------------------------------------------------------------------
# 0. 환경 변수 로드 & Assistant ID 검증 -----------------------------
# ------------------------------------------------------------------
load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

assistant_go_id       = os.getenv("ASSISTANT_GO_ID")
assistant_san_id      = os.getenv("ASSISTANT_INDUSTRIAL_ACCIDENT_ID")
assistant_petition_id = os.getenv("ASSISTANT_PETITION_ID")
assistant_review_id   = os.getenv("ASSISTANT_REVIEW_ID")
assistant_template_id = os.getenv("ASSISTANT_TEMPLATE_ID")

required_ids = {
    "ASSISTANT_GO_ID": assistant_go_id,
    "ASSISTANT_SAN_ID": assistant_san_id,
    "ASSISTANT_PETITION_ID": assistant_petition_id,
    "ASSISTANT_REVIEW_ID": assistant_review_id,
    "ASSISTANT_TEMPLATE_ID": assistant_template_id,
}
for env_name, value in required_ids.items():
    if not value:
        raise RuntimeError(f"환경 변수 {env_name} 누락")

# ------------------------------------------------------------------
# 1. FastAPI 인스턴스 & CORS 설정 -----------------------------------
# ------------------------------------------------------------------
app = FastAPI(title="Petition-AI Backend", version="1.0")
# allowed_origins = os.getenv("CORS_ORIGINS", "http://localhost:8087").split(",")
# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=allowed_origins,
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# 
# CORS 설정 - 기본 로컬 개발용 (localhost:8087만 허용)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8087"],  # 정적 origin 리스트
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ------------------------------------------------------------------
# 2. 데이터 모델 ----------------------------------------------------
# ------------------------------------------------------------------
class MessageRequest(BaseModel):
    thread_id: str  # OpenAI Thread ID
    message: str

class PetitionData(BaseModel):
    thread_id: str
    message: str
    extra_fields: Dict[str, str] = Field(...)

    @field_validator("extra_fields")
    @classmethod
    def check_keys(cls, v):
        must = {"name", "contact", "date"}
        missing = must - v.keys()
        if missing:
            raise ValueError(f"필수 항목 누락: {', '.join(missing)}")
        return v

class ApplyPatch(BaseModel):
    thread_id: str
    patched_fields: Dict[str, str]

class TemplateRequest(BaseModel):
    thread_id: str
    template_type: str

# ------------------------------------------------------------------
# 3. 상태 저장소 ----------------------------------------------------
# ------------------------------------------------------------------
# thread_id 별로 사용자의 latest extra_fields 값을 저장하는 메모리 저장소
# ex) 문서 항목 수정 결과 반영 or 자동 생성 시 값 사용
context_state: Dict[str, Dict[str, str]] = {}

# 동시 요청이 들어올 경우 상태 변경 충돌을 막기 위해 asyncio.Lock 사용
context_lock = asyncio.Lock()

# ------------------------------------------------------------------
# 4. GPT 호출 공통 함수 --------------------------------------------
# ------------------------------------------------------------------
async def call_assistant(
    thread_id: str,
    content: str,
    assistant_id: str,
    timeout: int = 30,
) -> str:
    # 사용자 메시지를 OpenAI 쓰레드에 추가
    client.beta.threads.messages.create(thread_id=thread_id, role="user", content=content)

    # assistant 실행 요청
    run = client.beta.threads.runs.create(thread_id=thread_id, assistant_id=assistant_id)

    elapsed = 0.0
    while True:
        result = client.beta.threads.runs.retrieve(thread_id=thread_id, run_id=run.id)
        if result.status == "completed":
            break
        # 다른 비동기 작업을 방해하지 않기 위해 asyncio.sleep 사용
        await asyncio.sleep(0.5)
        elapsed += 0.5
        if elapsed > timeout:
            raise HTTPException(504, "Assistant 응답 지연")

    # 최신 응답 메시지 반환
    msgs = client.beta.threads.messages.list(thread_id=thread_id, order="desc")
    return msgs.data[0].content[0].text.value

# ------------------------------------------------------------------
# 5. 엔드포인트 -----------------------------------------------------
# ------------------------------------------------------------------
# 클라이언트에서 새로운 대화 세션을 시작할 때 호출
@app.post("/create_thread")
async def create_thread(_: Request):
    thread = client.beta.threads.create()
    return {"thread_id": thread.id}

# 고용노동 관련 질문을 할 때 사용되는 Q&A용 endpoint
@app.post("/ask_go")
async def ask_go(req: MessageRequest):
    ans = await call_assistant(req.thread_id, req.message, assistant_go_id)
    return {"answer": ans}

# 산재 관련 질문을 할 때 사용되는 Q&A용 endpoint
@app.post("/ask_san")
async def ask_san(req: MessageRequest):
    ans = await call_assistant(req.thread_id, req.message, assistant_san_id)
    return {"answer": ans}

# 진정서 항목들을 검토 assistant에게 보내 피드백을 받을 때 호출
@app.post("/review_input")
async def review_input(data: PetitionData):
    payload = json.dumps(data.extra_fields, ensure_ascii=False)
    raw = await call_assistant(data.thread_id, payload, assistant_review_id)
    return json.loads(raw)

# 검토 assistant가 제안한 수정을 사용자 수락 후 서버에 반영할 때 호출
@app.post("/apply_feedback")
async def apply_feedback(patch: ApplyPatch):
    async with context_lock:
        context_state.setdefault(patch.thread_id, {}).update(patch.patched_fields)
    return {"status": "updated"}

# 저장된 항목 기반으로 자동 진정 내용 초안을 생성할 때 호출
@app.post("/auto_template")
async def auto_template(req: TemplateRequest):
    fields = context_state.get(req.thread_id)
    if not fields:
        raise HTTPException(400, "extra_fields 없음. /apply_feedback 호출 필요")

    prompt = (
        f"다음 JSON 정보를 활용해 {req.template_type} 진정 내용 초안을 작성하세요.\n"
        f"{json.dumps(fields, ensure_ascii=False)}"
    )
    body = await call_assistant(req.thread_id, prompt, assistant_template_id)
    return {"template": body}

# 최종 진정서 본문 생성을 요청할 때 호출 (수정된 항목 포함)
@app.post("/generate_document")
async def generate_document(data: PetitionData):
    async with context_lock:
        context_state[data.thread_id] = data.extra_fields

    body = await call_assistant(data.thread_id, data.message, assistant_template_id)
    return {
        "document": {
            "이름": data.extra_fields.get("name", ""),
            "연락처": data.extra_fields.get("contact", ""),
            "작성일": data.extra_fields.get("date", ""),
            "본문": body,
        }
    }

# ------------------------------------------------------------------
# 6. 로깅 -----------------------------------------------------------
# ------------------------------------------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
