package com.esc.wmg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {


    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/goMain")
    public String goMain() {
        return "main";
    }   


}
