package com.esc.wmg.controller;

import com.esc.wmg.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5501")
@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private OpenAiService openAiService;

    // 프론트 fetch("/api/chat") 호출 대응
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return openAiService.getGptReply(request.getMessage());
    }

    // 요청 바디 클래스
    public static class ChatRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
