const THREAD_ID = document.querySelector(".thread_id").innerText;
const input = document.getElementById("user-input");
const sendBtn = document.getElementById("send-btn");
const container = document.querySelector(".chat-messages");

// 자동 스크롤 함수
function scrollToBottom() {
  const container = document.querySelector(".chat-messages");
  container.scrollTop = container.scrollHeight;
}

// 메시지 전송 함수
async function sendMessage() {
  const message = input.value.trim();
  if (!message) return;

  container.innerHTML += `
    <div class="message user">
      <div class="avatar">🙂</div>
      <div class="name">사용자</div>
      <div class="bubble">${message}</div>
    </div>
    <div class="message bot loading">
      <div class="avatar">🤖</div>
      <div class="bubble">로딩 중...</div>
    </div>
  `;
  scrollToBottom(); // 메시지 추가 후 스크롤

  input.value = "";
  // 비동기 요청을 통해 DB에 메시지를 저장합니다.
  try {
    const res = await fetch("http://localhost:8087/save_user_message", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        message: message,
        thread_id: THREAD_ID
      })
    });
    if (!res.ok) throw new Error("Failed to save user_message");
  } catch (error) {
    console.error("Error saving user_message:", error);
  }

  // 비동기 요청을 통해 챗봇의 응답을 가져옵니다.
  try {
    const res = await fetch("http://localhost:8000/ask_san", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        message: message,
        thread_id: THREAD_ID
      })
    });

    const data = await res.json();
    const rawResponse = data.answer;

    // bot의 응답을 db에 저장
    try {
      const res = await fetch("http://localhost:8087/save_bot_message", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          message: rawResponse,
          thread_id: THREAD_ID
        })
      });
      if (!res.ok) throw new Error("Failed to save bot_message");
    } catch (error) {
      console.error("Error saving bot_message:", error);
    }

    //  OpenAI API의 응답을 HTML로 변환
    const formattedResponse = convertToHtmlFromAiText(rawResponse);

    // 로딩 중 메시지를 챗봇의 응답으로 교체
    const loadingEl = container.querySelector(".message.bot.loading");
    if (loadingEl) {
      loadingEl.outerHTML = `
        <div class="message bot">
          <div class="avatar">🤖</div>
          <div class="bubble">${formattedResponse}</div>
        </div>
      `;
    }
    scrollToBottom();// 챗봇 응답 작성 후 스크롤
  } catch (err) {
    console.error(err);
    const loadingEl = container.querySelector(".message.bot.loading");
    if (loadingEl) {
      loadingEl.outerHTML = `
        <div class="message bot">
          <div class="avatar">🤖</div>
          <div class="bubble">오류가 발생했어요. 다시 시도해 주세요.</div>
        </div>
      `;
    }
  }

}

sendBtn.addEventListener("click", sendMessage);
input.addEventListener("keydown", function (e) {
  if (e.key === "Enter") {
    e.preventDefault();
    sendMessage();
  }
});


// 비동기 방식으로 이전 thread_id 가져오기
async function getAllThread() {
  try {
    const response = await fetch("http://localhost:8087/get_all_thread_id", {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });
    const data = await response.json();
    // 예시: [{thread_id, room_title, thread_at, room_info}, ...]
    renderMyCases(data);
  } catch (error) {
    console.error("Error fetching thread ID:", error);
  }
}

// 사례 목록을 패널에 렌더링
function renderMyCases(threads) {
  const panel = document.getElementById("my-case-panel");
  // 기존 내용(제목 strong)만 남기고 모두 삭제
  panel.querySelectorAll(".case-item").forEach(e => e.remove());

  threads.forEach(thread => {
    const date = new Date(thread.threadAt);
    const now = new Date();
    const diffMs = now - date;
    const diffMin = Math.floor(diffMs / (1000 * 60));
    const diffHour = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDay = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    const safeDiffDay = diffDay < 0 ? 0 : diffDay; // 음수 보정

    let dateText = "";
    if (thread.threadId === THREAD_ID) {
      dateText = "현재 대화 중";
    } else {
      if (safeDiffDay === 0) {
        if (diffHour === 0) {
          if (diffMin < 1) dateText = "방금 전 생성됨";
          else dateText = `${diffMin}분 전 생성됨`;
        } else {
          dateText = `${diffHour}시간 전 생성됨`;
        }
      } else if (safeDiffDay === 1) {
        dateText = "어제 생성됨";
      } else {
        dateText = `${safeDiffDay}일 전 생성됨`;
      }
    }
    // form 방식으로 렌더링
    panel.innerHTML += `
      <div class="case-item">
        <strong>${thread.roomTitle == null || thread.roomTitle === "" ? "고용 노동 상담" : thread.roomTitle}</strong>
        <small>${dateText}</small>
        ${thread.roomInfo ? thread.roomInfo.substring(0, 30) + "..." : ""}
        <form action="rechat?thread_id=${thread.threadId}" method="get" style="display:inline;">
          <input type="hidden" name="thread_id" value="${thread.threadId}">
          <button type="submit" style="background:none;border:none;color:#2563eb;cursor:pointer;padding:0;">계속하다 →</button>
        </form>
      </div>
    `;
  });
}


// 페이지 로드 시 단 한번 이전 대화 내역 불러오기
async function loadPreviousChat() {
  try {
    const response = await fetch(`http://localhost:8087/get_previous_chat?thread_id=${THREAD_ID}`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });
    const data = await response.json();
    // 예시: [{message, chatter}, ...]
    data.forEach(chat => {
      // chatter가 "bot"이면 챗봇 메시지, 이외는 유저로 간주
      const messageClass = chat.chatter === "bot" ? "bot" : "user";
      container.innerHTML += `
        <div class="message ${messageClass}">
          <div class="avatar">${chat.chatter === "bot" ? "🤖" : "🙂"}</div>
          <div class="name">${chat.chatter === "bot" ? "챗봇" : "사용자"}</div>
          <div class="bubble">${convertToHtmlFromAiText(chat.chat_content)}</div>
        </div>
      `;
    });
    scrollToBottom(); // 이전 대화 불러온 후 스크롤
  } catch (error) {
    console.error("Error loading previous chat:", error);
  }
}
//  OpenAI API의 응답을 HTML로 변환 함수
function convertToHtmlFromAiText(rawResponse) {
  const formattedResponse = rawResponse
    // ** 내용 **을 <strong>로 변환
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    // * 내용 *을 <em>로 변환
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    // --- 을 <hr>로 변환 (가로줄)
    .replace(/---/g, '<hr style="border: 1px solid #e5e7eb; margin: 1rem 0;">')
    // #^n을 Hn으로 변환
    .replace(/^### (.*)$/gm, '<h3>$1</h3>') // H3
    .replace(/^## (.*)$/gm, '<h2>$1</h2>') // H2
    .replace(/^# (.*)$/gm, '<h1>$1</h1>') // H1
    // \n 줄바꿈을 <br>로 변환
    .replace(/\n/g, "<br>")
    // 링크 변환
    .replace(
      /(https?:\/\/[^\s\)<]+)/g, // 괄호 포함 안 함
      '<a href="$1" target="_blank" style="color:#2563eb; text-decoration:underline;">$1</a>'
    );

  return formattedResponse;
}

// html 로드 시 함수 호출
document.addEventListener("DOMContentLoaded", () => {
  getAllThread();
  loadPreviousChat();
});
