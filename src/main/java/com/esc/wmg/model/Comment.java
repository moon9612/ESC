package com.esc.wmg.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private long cmtIdx;
    private long postIdx;
    private String cmt_content;
    private LocalDateTime created_at;
    private String email;
    private long parentIdx;

}
