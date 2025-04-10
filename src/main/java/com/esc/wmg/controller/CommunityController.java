package com.esc.wmg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CommunityController {
    // 커뮤니티 메인 페이지로 이동
    @GetMapping("/goCommunity")
    public String goCommunity() {
        return "community";
    }
    
}   
