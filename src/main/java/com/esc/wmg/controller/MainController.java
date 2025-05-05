package com.esc.wmg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esc.wmg.entity.NewsEntity;
import com.esc.wmg.entity.PostEntity;
import com.esc.wmg.repository.NewsRepository;
import com.esc.wmg.repository.PostRepository;

@Controller
public class MainController {

    @Autowired
    PostRepository repository;

    @Autowired
    private NewsRepository newsRepository;

    // 초기 메인 페이지
    @GetMapping("/")
    public String loginForm(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "category", defaultValue = "all") String category,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, 3);
        Page<PostEntity> postPage = repository.findAll(pageable);

        // 뉴스 데이터 조회 
        List<NewsEntity> newsList = newsRepository.findAll();
        model.addAttribute("newsList", newsList);

        // 1) 게시글 목록
        List<PostEntity> posts = postPage.getContent();
        model.addAttribute("post_list", posts);
        model.addAttribute("category", category);

        return "main";
    }

}
