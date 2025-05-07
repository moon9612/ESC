package com.esc.wmg.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_news_cluster")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsClusterEntity {
    @Column(name = "date", updatable = false)
    private LocalDateTime date;


}
