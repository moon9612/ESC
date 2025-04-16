package com.esc.wmg.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONObject;

@Service
public class OpenAiService {
    private String API_KEY;
    private String ASSISTANT_ID;

    public OpenAiService() {

        Dotenv dotenv = Dotenv.configure().load();
        this.API_KEY = dotenv.get("OPENAI_API_KEY");
        this.ASSISTANT_ID = dotenv.get("ASSISTANT_ID");
        System.out.println("API_KEY: " + API_KEY);
        System.out.println("ASSISTANT_ID: " + ASSISTANT_ID);
    }

    public String getGptReply(String userMessage) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("OpenAI-Beta", "assistants=v2");

            // 1. 스레드 생성
            HttpEntity<String> threadRequest = new HttpEntity<>("{}", headers);
            ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                    "https://api.openai.com/v1/threads", threadRequest, String.class);
            String threadId = new JSONObject(threadResponse.getBody()).getString("id");

            // 2. 사용자 메시지 전송
            JSONObject messageJson = new JSONObject();
            messageJson.put("role", "user");
            messageJson.put("content", userMessage);
            HttpEntity<String> messageRequest = new HttpEntity<>(messageJson.toString(), headers);
            restTemplate.postForEntity(
                    "https://api.openai.com/v1/threads/" + threadId + "/messages", messageRequest, String.class);

            // 3. Run 생성
            JSONObject runJson = new JSONObject();
            runJson.put("assistant_id", ASSISTANT_ID);
            HttpEntity<String> runRequest = new HttpEntity<>(runJson.toString(), headers);
            ResponseEntity<String> runResponse = restTemplate.postForEntity(
                    "https://api.openai.com/v1/threads/" + threadId + "/runs", runRequest, String.class);
            String runId = new JSONObject(runResponse.getBody()).getString("id");

            // 4. Run 상태 확인
            String status = "";
            int waitCount = 0;
            while (!status.equals("completed") && waitCount < 120) {
                Thread.sleep(500);
                ResponseEntity<String> checkRun = restTemplate.exchange(
                        "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId,
                        HttpMethod.GET, new HttpEntity<>(headers), String.class);
                JSONObject checkJson = new JSONObject(checkRun.getBody());
                status = checkJson.getString("status");
                waitCount++;
            }

            if (!status.equals("completed")) {
                return "답변 생성 시간이 초과되었습니다.";
            }

            // 5. 응답 가져오기
            ResponseEntity<String> messagesResponse = restTemplate.exchange(
                    "https://api.openai.com/v1/threads/" + threadId + "/messages",
                    HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseBody = messagesResponse.getBody();

            JSONObject responseJson = new JSONObject(responseBody);
            JSONObject latestMessage = responseJson
                    .getJSONArray("data")
                    .getJSONObject(0);

            if (!latestMessage.has("content")) {
                return "답변 내용이 없습니다.";
            }

            JSONObject content = latestMessage
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getJSONObject("text");

            return content.getString("value");

        } catch (Exception e) {
            e.printStackTrace();
            return "죄송합니다. 답변 생성 중 오류가 발생했어요.";
        }
    }
}
