package com.esc.wmg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.model.User;
import com.esc.wmg.repository.UserRepository;

<<<<<<< HEAD
=======
import jakarta.servlet.http.HttpSession;
>>>>>>> test

@Controller
public class UserController {

    @Autowired
    UserRepository repository;
    
<<<<<<< HEAD

    // 회원정보 수정기능
    @PostMapping("/UserUpdate")
    public String UserUpdate() {

        return "";
    }
    

    // 회원가입 기능
=======
    //회원가입
>>>>>>> test
    @PostMapping("/userInsert")
    public String userInsert(UserEntity entity) {
        System.out.println(entity.toString());
        repository.save(entity);
        return "redirect:/";
    }
<<<<<<< HEAD
=======
    //로그인
    @PostMapping("/userSelect")
    public String userSelect(User user, HttpSession session) {
        UserEntity userEntity = repository.findByEmailAndPw(user.getEmail(), user.getPw());
        session.setAttribute("loginUser", userEntity);
        System.out.println(userEntity.toString());
        return "loginSuccess";
    }
>>>>>>> test

    @GetMapping("/")
    public String loginForm() {
        return "login";
    }
}
