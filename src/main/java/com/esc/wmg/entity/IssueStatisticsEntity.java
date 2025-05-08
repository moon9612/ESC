package com.esc.wmg.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "tbl_statistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueStatisticsEntity {
    // keyword, title, range, url, created_at 컬럼을 가진 테이블 
    
    @NonNull
    @Column(name = "keyword")
    private String keyword;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "range")
    private String range;
    @Id
    @Column(name = "url")
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
