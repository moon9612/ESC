package com.esc.wmg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.model.User;
import com.esc.wmg.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {

    @Autowired
    UserRepository repository;


    // 마이페이지 이동
    @GetMapping("goMyPage")
    public String getMethodName() {
        return "MyPage";
    }
    
    // 회원정보 수정기능
    @PostMapping("/userUpdate")
    public String userUpdate(UserEntity entity, HttpSession session) {
        repository.save(entity);
        session.setAttribute("loginUser", entity);
        return "redirect:/"; 
    }
    

    // 회원가입 기능
    @PostMapping("/userInsert")
    public String userInsert(UserEntity entity) {
        System.out.println(entity.toString());
        repository.save(entity);
        return "redirect:/";
    }
    // 로그인 기능
    @PostMapping("/userSelect")
    public String userSelect(User user, HttpSession session) {
        UserEntity userEntity = repository.findByEmailAndPw(user.getEmail(), user.getPw());
        session.setAttribute("loginUser", userEntity);
        System.out.println(userEntity.toString());
        return "loginSuccess";
    }

    @GetMapping("/")
    public String loginForm() {
        return "main";
    }
}
