package com.esc.wmg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IssueController {

    // Issue 페이지 이동
    @GetMapping("/issues")
    public String issuesPage() {
        return "issues"; // issue.html로 이동
    }
}
