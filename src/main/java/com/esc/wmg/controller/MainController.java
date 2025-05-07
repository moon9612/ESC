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
import com.esc.wmg.entity.NewsIssueEntity;
import com.esc.wmg.entity.PostEntity;
import com.esc.wmg.repository.NewsIssueRepository;
import com.esc.wmg.repository.NewsRepository;
import com.esc.wmg.repository.PostRepository;

@Controller
public class MainController {

    @Autowired
    PostRepository repository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsIssueRepository newsIssueRepository;

    // 초기 메인 페이지
    @GetMapping("/")
    public String loginForm(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "category", defaultValue = "all") String category,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, 3);
        Page<PostEntity> postPage = repository.findAll(pageable);

        // 뉴스 데이터 조회 (마지막 10개만 가져오기)
        List<NewsEntity> newsList = newsRepository.findAll();
        int totalNews = newsList.size();
        // 데이터가 10개 이상이면 마지막 10개를 가져오고, 10개 미만이면 모두 가F져오기
        List<NewsEntity> lastTenNews = (totalNews > 10)
                ? newsList.subList(totalNews - 10, totalNews)
                : newsList;  // 10개 미만일 경우 모두 가져오기
        model.addAttribute("newsList", lastTenNews);

        List<NewsIssueEntity> newsKeywords = newsIssueRepository.findTop10ByOrderByDateDescRnkAsc();
        model.addAttribute("newsKeywords", newsKeywords);

        // 1) 게시글 목록
        List<PostEntity> posts = postPage.getContent();
        model.addAttribute("post_list", posts);
        model.addAttribute("category", category);

        return "main";
    }

}
