// // ChatController.java
// package com.esc.wmg.controller;

// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.esc.wmg.service.OpenAiJavaService;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;

// // Openai-java-api용 사용 X
// @CrossOrigin(origins = "http://127.0.0.1:5501")
// @RestController
// @RequestMapping("/api")
// public class ChatJavaController {

//     @Autowired
//     private OpenAiJavaService openAiService;


//     // 채팅메시지, 쓰레드 아이디
//     @PostMapping("/chat")
//     public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
//         Map<String, String> response = openAiService.getGptReply(request.getMessage(), request.getThreadId());
//         return ResponseEntity.ok(response);
//     }

//     public static class ChatRequest {
//         private String message;
//         private String threadId; // 🔹 추가

//         public String getMessage() {
//             return message;
//         }

//         public void setMessage(String message) {
//             this.message = message;
//         }

//         public String getThreadId() {
//             return threadId;
//         }

//         public void setThreadId(String threadId) {
//             this.threadId = threadId;
//         }
//     }
// }