package com.esc.wmg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IssueController {

    // Issue 페이지 이동
    @GetMapping("/issues")
    public String issuesPage() {
        // issue 키워드는 main에서 로드해서 session에 저장된 상태
        // issue 키워드 관련 뉴스 로드
        
        return "issues"; // issue.html로 이동
    }
}
