package com.esc.wmg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    UserRepository repository;

    // 회원가입 기능
    @PostMapping("/userInsert")
    public String userInsert(UserEntity entity) {
        System.out.println("폼 데이터: " + toString());
    
        repository.save(entity); // 기존 테이블(tbl_user)에 insert됨
        return "redirect:/";
    }
    

    // 로그인 페이지로 이동
    @GetMapping("/goLogin")
    public String goLogin() {
        return "login";
    }

    // 초기 메인 페이지
    @GetMapping("/")
    public String loginForm() {
        return "main";
    }

}
