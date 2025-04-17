package com.esc.wmg.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    private long post_idx;
    private String post_category;
    private String post_title;
    private String post_content;
    private String post_file;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private int post_views;
    private String post_email;
}
