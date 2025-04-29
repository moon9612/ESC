package com.esc.wmg.controller;

import com.esc.wmg.service.NewsService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/api/news/save-csv")
    public String saveCsvToDb(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        newsService.saveCsvData(filePath);
        return "✅ CSV 데이터가 저장되었습니다!";
    }
}
