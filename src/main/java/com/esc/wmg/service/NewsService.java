package com.esc.wmg.service;

import com.esc.wmg.entity.NewsEntity;
import com.esc.wmg.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    @Transactional
    public void saveCsvData(String filePath) {
        List<NewsEntity> newsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // 안정적인 CSV 분리

                if (fields.length >= 6) {
                    // news_idx는 자동으로 생성되므로 설정하지 않습니다.
                    NewsEntity news = new NewsEntity(
                            fields[2].replaceAll("\"", "").trim(), // news_title
                            fields[1].replaceAll("\"", "").trim(), // news_writer
                            fields[4].replaceAll("\"", "").trim(), // news_url
                            fields[5].replaceAll("\"", "").trim(), // news_img
                            fields[0].replaceAll("\"", "").trim() // date
                    );
                    newsList.add(news);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        newsRepository.saveAll(newsList);
    }
}
