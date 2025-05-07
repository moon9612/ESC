package com.esc.wmg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_news_cluster")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TblNewsClusterId.class)
public class NewsClusterEntity {

    @Id
    @Column(name = "date", nullable = false, length = 8, updatable = false)
    private String date;  // String으로 변경

    @Id
    @Column(name = "mdl_index", nullable = false, updatable = false)
    private Integer mdlIndex;

    @Id
    @Column(name = "seq", nullable = false, updatable = false)
    private Integer seq;

    @Column(name = "title", nullable = false, length = 500, updatable = false)
    private String title;

    @Column(name = "url", nullable = false, length = 1000, updatable = false)
    private String url;

    @Column(name = "similarity", nullable = true, updatable = false)
    private Float similarity;  // Float로 변경
}
