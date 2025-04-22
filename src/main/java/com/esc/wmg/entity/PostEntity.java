package com.esc.wmg.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "tbl_post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long post_idx;

    @NonNull
    @Column(name = "post_category", nullable = false)
    private String post_category;

    @NonNull
    @Column(name = "post_title")
    private String post_title;

    @NonNull
    @Column(name = "post_content")
    private String post_content;

    @Column(name = "post_file")
    private String post_file;

    @NonNull
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @NonNull
    @UpdateTimestamp
    @Column(name = "updated_at", updatable = true)
    private LocalDateTime updated_at;

    @Column(name = "post_views")
    private int post_views;

    @Column(name = "post_likes", nullable = false)
    private int post_likes = 0;

    @NonNull
    @Column(name = "post_email", nullable = false)
    private String post_email;

}
