package com.esc.wmg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatbotController {

    @GetMapping("/document")
    public String document() {
        return "document";
    }

    @GetMapping("/document2-1")
    public String document21() {
        return "document2-1";
    }

    @GetMapping("/document2-2")
    public String document22() {
        return "document2-2";
    }

    @GetMapping("/minichat")
    public String minichat() {
        return "minichat";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/index2-1")
    public String index21() {
        return "index2-1";
    }

    @GetMapping("/index2-2")
    public String index22() {
        return "index2-2";
    }
    @GetMapping("/main")
    public String main() {
        return "main"; // → templates/main.html 로 이동
    }
}
