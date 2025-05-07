package com.esc.wmg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esc.wmg.entity.NewsClusterEntity;
import com.esc.wmg.entity.NewsIssueEntity;
import com.esc.wmg.repository.NewsClusterRepository;
import com.esc.wmg.repository.NewsIssueRepository;


@Controller
public class IssueController {

    @Autowired
    private NewsClusterRepository newsClusterRepository;
    @Autowired
    private NewsIssueRepository newsIssueRepository;

    // Issue 페이지 이동
    @GetMapping("/issues")
    public String issuesPage(Model model) {
        // issue 키워드는 main에서 로드해서 session에 저장된 상태
        // issue 키워드 관련 뉴스 로드
        List<NewsClusterEntity> newsCluster = newsClusterRepository.findAll();
        System.out.println("newsCluster: " + newsCluster.size()); // 디버깅용 출력
        model.addAttribute("newsCluster", newsCluster); // 뉴스 클러스터 리스트를 모델에 추가
        
        List<NewsIssueEntity> newsKeywords = newsIssueRepository.findTop10ByOrderByDateDescRnkAsc();
        model.addAttribute("newsKeywords", newsKeywords);
        return "issues"; // issue.html로 이동
    }
}
