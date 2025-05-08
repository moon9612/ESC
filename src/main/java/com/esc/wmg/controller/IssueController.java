package com.esc.wmg.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.esc.wmg.entity.IssueStatisticsEntity;
import com.esc.wmg.entity.NewsClusterEntity;
import com.esc.wmg.entity.NewsIssueEntity;
import com.esc.wmg.repository.IssueStatisticsRepository;
import com.esc.wmg.repository.NewsClusterRepository;
import com.esc.wmg.repository.NewsIssueRepository;

@Controller
public class IssueController {

    @Autowired
    private NewsClusterRepository newsClusterRepository;
    @Autowired
    private NewsIssueRepository newsIssueRepository;
    @Autowired
    private IssueStatisticsRepository issueStatisticsRepository;

    // Issue 페이지 이동
    @GetMapping("/issues")
    public String issuesPage(Model model) {
        // issue 키워드는 main에서 로드해서 session에 저장된 상태
        // issue 키워드 관련 뉴스 로드
        List<NewsClusterEntity> newsCluster = newsClusterRepository.findAllByOrderBySimilarityDesc()
            .stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(NewsClusterEntity::getTitle, n -> n, (a, b) -> a),
                m -> new ArrayList<>(m.values())
            ));
        System.out.println("newsCluster: " + newsCluster.size()); // 디버깅용 출력
        model.addAttribute("newsCluster", newsCluster); // 뉴스 클러스터 리스트를 모델에 추가
        // issue 키워드 관련 뉴스 로드
        List<IssueStatisticsEntity> issueStatistics = issueStatisticsRepository.findAll();
        System.out.println("issueStatistics: " + issueStatistics.size()); // 디버깅용 출력
        model.addAttribute("issueStatistics", issueStatistics); // 뉴스 클러스터 리스트를 모델에 추가
        //이슈 키워드 10개 로드
        List<NewsIssueEntity> newsKeywords = newsIssueRepository.findTop10ByOrderByDateDescRnkAsc();
        model.addAttribute("newsKeywords", newsKeywords);
        return "issues"; // issue.html로 이동
    }
}
