import time
import os
import json
from fastapi import FastAPI, Request, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from openai import OpenAI
from dotenv import load_dotenv

# -----------------------
# 초기화
# -----------------------
load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

assistant_go_id = os.getenv("ASSISTANT_GO_ID")                 # 고용노동 Q&A
assistant_san_id = os.getenv("ASSISTANT_INDUSTRIAL_ACCIDENT_ID")  # 산재 Q&A
assistant_review_id = os.getenv("ASSISTANT_REVIEW_ID")           # 항목 검토 전용 (JSON)
assistant_template_id = os.getenv("ASSISTANT_TEMPLATE_ID")       # 자동 작성 전용

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8087"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -----------------------
# 데이터 모델
# -----------------------
class MessageRequest(BaseModel):
    thread_id: str
    message: str

class PetitionData(BaseModel):
    thread_id: str
    message: str
    extra_fields: dict  # 고정 입력 폼 값

# -----------------------
# 상태 저장 (thread별 최신 extra_fields)
# -----------------------
context_state: dict[str, dict] = {}

# -----------------------
# 공통 대화 처리
# -----------------------
async def call_assistant(thread_id: str, content: str, assistant_id: str) -> str:
    """assistant 호출 후 완료될 때까지 polling → 텍스트 응답 반환"""
    client.beta.threads.messages.create(
        thread_id=thread_id,
        role="user",
        content=content
    )
    run = client.beta.threads.runs.create(
        thread_id=thread_id,
        assistant_id=assistant_id
    )
    while True:
        result = client.beta.threads.runs.retrieve(thread_id=thread_id, run_id=run.id)
        if result.status == "completed":
            break
        time.sleep(0.3)
    response = client.beta.threads.messages.list(thread_id=thread_id)
    return response.data[0].content[0].text.value

# -----------------------
# 기본 엔드포인트
# -----------------------
@app.post("/create_thread")
async def create_thread(_: Request):
    thread = client.beta.threads.create()
    return {"thread_id": thread.id}

# 고용노동/산재 Q&A (thread 공유)
@app.post("/ask_go")
async def ask_go(req: MessageRequest):
    answer = await call_assistant(req.thread_id, req.message, assistant_go_id)
    return {"answer": answer}

@app.post("/ask_san")
async def ask_san(req: MessageRequest):
    answer = await call_assistant(req.thread_id, req.message, assistant_san_id)
    return {"answer": answer}

# -----------------------
# 1. 항목 검토 (JSON)
# -----------------------
@app.post("/review_input")
async def review_input(data: PetitionData):
    """검토 assistant 호출 → JSON 피드백 반환 (extra_fields는 그대로)"""
    try:
        feedback_raw = await call_assistant(
            data.thread_id,
            json.dumps(data.extra_fields, ensure_ascii=False),
            assistant_review_id
        )
        # 검토 결과 JSON 스트링을 그대로 반환
        return json.loads(feedback_raw)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# -----------------------
# 2. 검토 수락 → extra_fields 즉시 업데이트
# -----------------------
class ApplyPatch(BaseModel):
    thread_id: str
    patched_fields: dict  # 검토 assistant가 제안한 수정 반영본

@app.post("/apply_feedback")
async def apply_feedback(patch: ApplyPatch):
    """프론트에서 '수락' 누르면 호출 → 서버 메모리 업데이트"""
    context_state[patch.thread_id] = patch.patched_fields
    return {"status": "updated"}

# -----------------------
# 3. 템플릿 자동 작성
# -----------------------
class TemplateRequest(BaseModel):
    thread_id: str
    template_type: str  # 예: "임금체불" 등

@app.post("/auto_template")
async def auto_template(req: TemplateRequest):
    fields = context_state.get(req.thread_id)
    if not fields:
        raise HTTPException(status_code=400, detail="extra_fields 정보가 없습니다. /apply_feedback 먼저 호출하세요")

    prompt = f"""다음 JSON 정보를 활용해 {req.template_type} 진정 내용 초안을 작성하세요.\n{json.dumps(fields, ensure_ascii=False)}"""
    try:
        body = await call_assistant(req.thread_id, prompt, assistant_template_id)
        return {"template": body}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# -----------------------
# 4. 최종 문서 생성
# -----------------------
@app.post("/generate_document")
async def generate_document(data: PetitionData):
    """본문 작성 assistant 호출 → extra_fields와 결합해 반환"""
    # state 최신화 (검토 수락이 없더라도 프론트 값 우선)
    context_state[data.thread_id] = data.extra_fields

    try:
        body = await call_assistant(data.thread_id, data.message, assistant_template_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    doc = {
        "이름": data.extra_fields.get("name", ""),
        "연락처": data.extra_fields.get("contact", ""),
        "작성일": data.extra_fields.get("date", ""),
        "본문": body
    }
    return {"document": doc}
