package com.esc.wmg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class chatPageController {

    @GetMapping("/goChat")
    public String goChat() {
        return "chat";
    }
    
}
