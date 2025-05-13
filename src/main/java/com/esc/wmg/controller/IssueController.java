package com.esc.wmg.controller;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

        // 오늘 날짜의 뉴스 클러스터를 similarity 내림차순으로 가져옴

        // 오늘 00:00:00
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        // 내일 00:00:00
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        // 해당 범위로 조회
        List<NewsClusterEntity> allNews = newsClusterRepository.findByDateBetweenOrderBySimilarityDesc(startOfDay,
                endOfDay);

        // (mdlIndex, seq)별로 최신 날짜의 뉴스만 남기기
        Map<String, NewsClusterEntity> latestNewsByMdlIndexSeq = allNews.stream()
                .collect(Collectors.toMap(
                        n -> n.getMdlIndex() + "_" + n.getSeq(),
                        n -> n,
                        (n1, n2) -> n1.getDate().compareTo(n2.getDate()) >= 0 ? n1 : n2 // 날짜가 더 최신인 것 선택
                ));

        // similarity 내림차순 정렬로 리스트 변환
        List<NewsClusterEntity> newsCluster = latestNewsByMdlIndexSeq.values().stream()
                .sorted(Comparator.comparing(NewsClusterEntity::getSimilarity,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        System.out.println("newsCluster: " + newsCluster.size()); // 디버깅용 출력
        model.addAttribute("newsCluster", newsCluster); // 뉴스 클러스터 리스트를 모델에 추가
        // issue 키워드 관련 뉴스 로드
        List<IssueStatisticsEntity> issueStatistics = issueStatisticsRepository.findAll();
        System.out.println("issueStatistics: " + issueStatistics.size()); // 디버깅용 출력
        model.addAttribute("issueStatistics", issueStatistics); // 뉴스 클러스터 리스트를 모델에 추가
        // 이슈 키워드 10개 로드
        List<NewsIssueEntity> newsKeywords = newsIssueRepository.findTop10ByOrderByDateDescRnkAsc();
        model.addAttribute("newsKeywords", newsKeywords);
        return "issues"; // issue.html로 이동
    }
}
