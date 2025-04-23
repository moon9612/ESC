package com.esc.wmg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    UserRepository repository;

    // 로그인 기능
    @PostMapping("/userSelect")
    public String userSelect(UserEntity entity, HttpSession session, Model model) {
        UserEntity loginUser = repository.findByEmailAndPw(entity.getEmail(), entity.getPw());

        if (loginUser != null) {
            session.setAttribute("loginUser", loginUser);
            return "redirect:/";
        } else {
            return "login";
        }
    }

    // 로그아웃 기능
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 회원가입 기능
    @PostMapping("/userInsert")
    public String userInsert(UserEntity entity) {
        System.out.println(entity.toString());
        repository.save(entity);
        return "redirect:/";
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String goLogin() {
        return "login";
    }

    // 초기 메인 페이지
    @GetMapping("/")
    public String loginForm() {
        return "main";
    }

}
