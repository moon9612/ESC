package com.esc.wmg.controller;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.esc.wmg.entity.UserEntity;

import jakarta.servlet.http.HttpSession;



@Controller
public class chatPageController {

    // 1. 로그인 확인 + 2. 세션에서 theadId 가져오기(2-1. 없으면 생성, 2-2 세션에 저장) + 3.채팅 페이지로 이동
    @GetMapping("/chat")
    public String getThreadId(HttpSession session) {
        // 1. 세션에서 로그인 유저 확인
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 유저가 없으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }else{
            // 로그인 유저가 있으면 세션 thread_id 확인
        try {
            // 2. 세션에서 thread_id 확인
            String thread_id = (String) session.getAttribute("thread_id");

            // 2-1. thread_id가 없으면 외부 API에 요청하여 새로 생성
            if (thread_id == null) {
                // 외부 OpenAI 쓰레드 생성 API URL
                String threadApiUrl = "http://localhost:8000/create_thread";

                // Spring의 RestTemplate 객체 생성
                RestTemplate restTemplate = new RestTemplate();

                // HTTP 요청 헤더 설정 (POST 요청으로 JSON 전송)
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // POST 요청 바디: 빈 JSON 객체
                HttpEntity<String> request = new HttpEntity<>("{}", headers);

                // 외부 서버에 POST 요청 보내기
                ResponseEntity<String> response = restTemplate.postForEntity(threadApiUrl, request, String.class);

                // 2-2. 응답이 성공이면 thread_id 추출 → 세션에 저장
                if (response.getStatusCode().is2xxSuccessful()) {
                    JSONObject json = new JSONObject(response.getBody());
                    thread_id = json.getString("thread_id");

                    // 세션에 저장하여 이후 요청에서도 재사용 가능
                    session.setAttribute("thread_id", thread_id);
                } else {
                    // 실패한 경우 로그 출력
                    System.err.println("Thread 생성 실패: " + response.getStatusCode());
                }
            }

        } catch (Exception e) {
            // 예외 발생 시 스택 추적 출력
            e.printStackTrace();
        }

        // 3. chat.html 템플릿으로 이동
        return "chat";
    }
    }
}
