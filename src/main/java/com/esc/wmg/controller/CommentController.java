package com.esc.wmg.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esc.wmg.entity.CommentEntity;
import com.esc.wmg.repository.CommentRepository;

@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/sendComment")
    public String sendComment(
            @RequestParam(value = "postIdx", required = false) String postIdxStr,
            @RequestParam("cmt_comment") String cmt_content,
            @RequestParam("email") String email
    ) {
        Long postIdx = 0L; // 기본값 설정

        // postIdxStr이 null이 아니고, 숫자 형식일 경우에만 변환
        if (postIdxStr != null && !postIdxStr.isEmpty()) {
            try {
                postIdx = Long.parseLong(postIdxStr);
            } catch (NumberFormatException e) {
                // postIdxStr이 숫자 형식이 아닌 경우 예외 처리 (로깅 또는 기본값 유지)
                System.out.println("Invalid postIdx format: " + postIdxStr);
            }
        }

        // email이 비어 있지 않거나 유효하지 않다면 오류 처리
        if (email == null || email.isEmpty()) {
            // 이메일이 유효하지 않은 경우 처리 (예: 로그인 페이지로 리다이렉트)
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        CommentEntity comment = new CommentEntity();
        comment.setPostIdx(postIdx);
        comment.setCmt_content(cmt_content);
        comment.setEmail(email);
        comment.setCreated_at(LocalDateTime.now());
        // comment.setParent_idx(0); // 댓글이 최상위 댓글인 경우 0, 부모 댓글이 있으면 해당 부모 댓글 idx로 설정

        commentRepository.save(comment);

        // 댓글 작성 후 해당 게시글로 리다이렉트
        return "redirect:/postContent?post_idx=" + postIdx;
    }

}
