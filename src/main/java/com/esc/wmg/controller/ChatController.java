package com.esc.wmg.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.repository.ChatRepository;
import com.esc.wmg.entity.ChatEntity;
import com.esc.wmg.entity.ThreadEntity;
import com.esc.wmg.service.ThreadService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    private final ChatRepository chatRepository;

    @Autowired
    private ThreadService threadService;

    ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // // 1. 로그인 확인 + 2. 세션에서 theadId 가져오기(2-1. 없으면 생성, 2-2 세션에 저장) + 3.채팅 페이지로 이동
    // @GetMapping("/goChat")
    // public String getThreadId(HttpSession session) {
    //     // 1. 세션에서 로그인 유저 확인
    //     UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
    //     ThreadEntity threadEntity = null;
    //     if (loginUser == null) {
    //         // 로그인 유저가 없으면 로그인 페이지로 리다이렉트
    //         return "redirect:/login";
    //     }else{
    //         // 로그인 유저가 있으면 세션 thread_id 확인
    //     try {
    //         // 2. 세션에서 thread_id 확인
    //         final String thread_id = (String) session.getAttribute("thread_id");
    //         // 2-1. thread_id가 없으면 외부 API에 요청하여 새로 생성
    //         if (thread_id == null) {
    //             // 외부 OpenAI 쓰레드 생성 API URL
    //             String threadApiUrl = "http://localhost:8000/create_thread";

    //             // Spring의 RestTemplate 객체 생성
    //             RestTemplate restTemplate = new RestTemplate();

    //             // HTTP 요청 헤더 설정 (POST 요청으로 JSON 전송)
    //             HttpHeaders headers = new HttpHeaders();
    //             headers.setContentType(MediaType.APPLICATION_JSON);

    //             // POST 요청 바디: 빈 JSON 객체
    //             HttpEntity<String> request = new HttpEntity<>("{}", headers);

    //             // 외부 서버에 POST 요청 보내기
    //             ResponseEntity<String> response = restTemplate.postForEntity(threadApiUrl, request, String.class);
    //             // 2-2. thread_id  생성 응답이 성공이면 thread_id 추출 → 세션에 저장
    //             if (response.getStatusCode().is2xxSuccessful()) {
    //                 JSONObject json = new JSONObject(response.getBody());
    //                 String threadId = json.getString("thread_id");

    //                 // 세션에 thread_id 저장
    //                 session.setAttribute("thread_id", threadId);

    //                 // DB에 thread_id 저장
    //                 threadEntity = new ThreadEntity(threadId, loginUser.getEmail());
    //                 System.out.println("ThreadEntity: " + threadEntity);
    //                 threadService.saveThreadId(threadEntity);
    //                 // 3. chat.html 템플릿으로 이동
    //                 return "redirect:/chat?thread_id=" + threadEntity.getThread_id();
    //             } else {
    //                 // 실패한 경우 로그 출력
    //                 System.err.println("Thread 생성 실패: " + response.getStatusCode());
    //                 return "main";
    //             }
    //         }else{
    //             // 2-3. thread_id가 있으면 DB에서 threadEntity 조회
    //             threadEntity = threadService.getAllThreadByEmail(loginUser.getEmail()).stream()
    //                     .filter(thread -> thread.getThread_id().equals(thread_id))
    //                     .findFirst()
    //                     .orElse(null);
    //             // 2-4. 세션에 thread_id 저장
    //             session.setAttribute("thread_id", thread_id);
    //             // 2-5. thread_id로 DB에서 채팅 내역 조회
    //             List<ChatEntity> tbl_chat = threadService.getChatByThreadId(thread_id);
    //             // 2-6. 세션에 tbl_chat 저장
    //             session.setAttribute("tbl_chat", tbl_chat);
    //             // 3. chat.html 템플릿으로 이동
    //             return "redirect:/chat?thread_id=" + thread_id;
    //         }
    //     } catch (Exception e) {
    //         // 예외 발생 시 스택 추적 출력
    //         e.printStackTrace();
    //         System.out.println("에러");
    //         return "main";
    //     }     
    // }
    // }
    @Transactional
    @GetMapping("/goChat")
    public String getThreadId(HttpSession session) {
        System.out.println("[1] /goChat 진입");

        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        ThreadEntity threadEntity = null;

        if (loginUser == null) {
            System.out.println("[2] 로그인 유저 없음 → /login 리다이렉트");
            return "redirect:/login";
        }

        System.out.println("[2] 로그인 유저 확인 완료: " + loginUser.getEmail());

        try {
            final String threadId = (String) session.getAttribute("thread_id");
            System.out.println("[3] 세션에서 thread_id 확인: " + threadId);

            if (threadId == null) {
                System.out.println("[4] thread_id 없음 → FastAPI에 /create_thread 요청 시도");

                String threadApiUrl = "http://127.0.0.1:8000/create_thread"; // localhost → 127.0.0.1 수정
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<>("{}", headers);

                ResponseEntity<String> response = restTemplate.postForEntity(threadApiUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("[5] /create_thread 응답 성공");
                    JSONObject json = new JSONObject(response.getBody());
                    String newThreadId = json.getString("thread_id");

                    session.setAttribute("thread_id", newThreadId);
                    System.out.println("[6] 세션에 thread_id 저장 완료: " + newThreadId);

                    threadEntity = new ThreadEntity(newThreadId, loginUser.getEmail());
                    threadEntity.setRoomTitle("고용 노동 상담"); 
                    threadService.saveThreadId(threadEntity);
                    System.out.println("[7] DB에 ThreadEntity 저장 완료");

                    return "redirect:/chat?thread_id=" + newThreadId;
                } else {
                    System.err.println("[X-7] /create_thread 응답 실패: " + response.getStatusCode());
                    return "main";
                }
            } else {
                System.out.println("[8] 세션에 기존 thread_id 존재: " + threadId);
                threadEntity = threadService.getAllThreadByEmail(loginUser.getEmail()).stream()
                    .filter(thread -> thread.getThreadId().equals(threadId))
                    .findFirst()
                    .orElse(null);

                session.setAttribute("thread_id", threadId);
                List<ChatEntity> tbl_chat = threadService.getChatByThreadId(threadId);
                session.setAttribute("tbl_chat", tbl_chat);

                System.out.println("[9] DB에서 기존 Thread 및 채팅 내역 조회 완료 → chat.html 이동");
                return "redirect:/chat?thread_id=" + threadId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[X-9] 예외 발생 → main으로 리다이렉트");
            return "main";
        }
    }
    @Transactional
    @GetMapping("/goChatSan")
    public String goChatSan(HttpSession session) {
        System.out.println("[1] /goChatSan 진입");

        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        ThreadEntity threadEntity = null;

        if (loginUser == null) {
            System.out.println("[2] 로그인 유저 없음 → /login 리다이렉트");
            return "redirect:/login";
        }

        System.out.println("[2] 로그인 유저 확인 완료: " + loginUser.getEmail());

        try {
            final String threadId = (String) session.getAttribute("thread_id");
            System.out.println("[3] 세션에서 thread_id 확인: " + threadId);

            if (threadId == null) {
                System.out.println("[4] thread_id 없음 → FastAPI에 /create_thread 요청 시도");

                String threadApiUrl = "http://127.0.0.1:8000/create_thread"; // localhost → 127.0.0.1 수정
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<>("{}", headers);

                ResponseEntity<String> response = restTemplate.postForEntity(threadApiUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("[5] /create_thread 응답 성공");
                    JSONObject json = new JSONObject(response.getBody());
                    String newThreadId = json.getString("thread_id");

                    session.setAttribute("thread_id", newThreadId);
                    System.out.println("[6] 세션에 thread_id 저장 완료: " + newThreadId);

                    threadEntity = new ThreadEntity(newThreadId, loginUser.getEmail());
                    threadEntity.setRoomTitle("산업 재해 상담"); 
                    threadService.saveThreadId(threadEntity);
                    System.out.println("[7] DB에 ThreadEntity 저장 완료");

                    return "redirect:/chatSan?thread_id=" + newThreadId;
                } else {
                    System.err.println("[X-7] /create_thread 응답 실패: " + response.getStatusCode());
                    return "main";
                }
            } else {
                System.out.println("[8] 세션에 기존 thread_id 존재: " + threadId);
                threadEntity = threadService.getAllThreadByEmail(loginUser.getEmail()).stream()
                    .filter(thread -> thread.getThreadId().equals(threadId))
                    .findFirst()
                    .orElse(null);

                session.setAttribute("thread_id", threadId);
                List<ChatEntity> tbl_chat = threadService.getChatByThreadId(threadId);
                session.setAttribute("tbl_chat", tbl_chat);

                System.out.println("[9] DB에서 기존 Thread 및 채팅 내역 조회 완료 → chat.html 이동");
                return "redirect:/chatSan?thread_id=" + threadId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[X-9] 예외 발생 → main으로 리다이렉트");
            return "main";
        }
    }

    // get 방식의 thread_id를 통해 이전 채팅으로 돌아가기
    @Transactional
    @GetMapping("/rechat")
    public String returnToChat(@RequestParam("thread_id") String threadId, HttpSession session) {
        // 1. 세션에서 로그인 유저 확인
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 유저가 없으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 2. 세션에 thread_id 저장
        session.setAttribute("thread_id", threadId);
        // 2-1. thread_id로 DB에서 채팅 내역 조회
        List<ChatEntity> tbl_chat = threadService.getChatByThreadId(threadId);
        // 2-2. 세션에 tbl_chat 저장
        session.setAttribute("tbl_chat", tbl_chat);
        // 3. chat.html 템플릿으로 리다이렉트
        return "redirect:/chat?thread_id=" + threadId;
    }

    @GetMapping("/chat")
    public String chatPage(@RequestParam(value = "thread_id", required = false) String threadId, HttpSession session) {
        // 필요하다면 세션에 thread_id 저장
        if (threadId != null) {
            session.setAttribute("thread_id", threadId);
        }
        // chat.html 템플릿 반환
        return "chat";
    }
    @GetMapping("/chatSan")
    public String chatSan(@RequestParam(value = "thread_id", required = false) String threadId, HttpSession session) {
        // 필요하다면 세션에 thread_id 저장
        if (threadId != null) {
            session.setAttribute("thread_id", threadId);
        }
        // chat.html 템플릿 반환
        return "chatSan";
    }
    // session.loginUser.email을 통해 모든 thread_id 반환하기(비동기)
    @GetMapping("/get_all_thread_id")
    @ResponseBody
    @Transactional
    public List<ThreadEntity> getAllThreadId(HttpSession session) {
        // 1. 세션에서 로그인 유저 확인
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null ) {
            return List.of(); // 로그인 안 된 경우 빈 리스트 반환
        }

        // 2. 이메일로 thread 목록 조회
        String email = loginUser.getEmail();
        List<ThreadEntity> threads = threadService.getAllThreadByEmail(email);

        // 3. 리스트를 JSON으로 자동 변환하여 반환
        return threads;
    }


    // thread_id를 통해 채팅 내역 반환하기(비동기)
    @GetMapping("/get_chat_history")
    @ResponseBody
    @Transactional
    public List<ChatEntity> getChatHistory(@RequestParam("thread_id") String threadId) {

        // 1. thread_id로 채팅 내역 조회
        List<ChatEntity> chatHistory = threadService.getChatByThreadId(threadId);

        // 2. 리스트를 JSON으로 자동 변환하여 반환
        return chatHistory;
    }
    // 유저의 입력 메시지 DB 저장하기(비동기)
    @PostMapping("/save_user_message")
    @ResponseBody
    @Transactional
    public String saveUserMessage(@RequestBody Map<String, Object> payload, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        // 1. 요청에서 데이터 추출
        String threadId = (String) payload.get("thread_id");
        String message = (String) payload.get("message");
        String chatter = loginUser.getEmail();

        // 2. ChatEntity 생성 및 저장
        ChatEntity chat = new ChatEntity(
            threadId,           // threadId
            chatter,           // chatter
            message           // chat_content
        );
        // chat_idx, created_at은 자동 생성

        chatRepository.save(chat);

        return "ok";
    }
    // 봇의 응답 메시지 DB 저장하기(비동기)
    @PostMapping("/save_bot_message")
    @ResponseBody
    @Transactional
    public String saveBotMessage(@RequestBody Map<String, Object> payload, HttpSession session) {

        // 1. 요청에서 데이터 추출
        String threadId = (String) payload.get("thread_id");
        String message = (String) payload.get("message");
        String chatter = "bot";
        // 2. ChatEntity 생성 및 저장
        ChatEntity chat = new ChatEntity(
            threadId,          // threadId
            chatter,          // chatter
            message          // chat_content
        );
        // chat_idx, created_at은 자동 생성

        chatRepository.save(chat);

        return "ok";
    }
    // 채팅 내역 불러오기(비동기)
    @GetMapping("/get_previous_chat")
    @ResponseBody
    @Transactional
    public List<ChatEntity> getPreviousChat(@RequestParam("thread_id") String threadId) {
        // 1. thread_id로 DB에서 채팅 내역 조회
        List<ChatEntity> chatHistory = threadService.getChatByThreadId(threadId);
        // 2. 리스트를 JSON으로 자동 변환하여 반환
        return chatHistory;
    }
}
