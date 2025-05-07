package com.esc.wmg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_news_issue")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsIssueEntity {

    @Id
    @Column(name = "mdl_index", nullable = false)
    private long mdlIndex;

    @Column(name = "issue_keyword", nullable = false)
    private String issueKeyword;

    @Column(name = "rnk", nullable = false)
    private int rank;

    @Column(name = "date", nullable = false)
    private String date;
}
