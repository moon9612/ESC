package com.esc.wmg.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esc.wmg.entity.CommentEntity;
import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.repository.CommentRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    // 댓글 삭제 기능
    @PostMapping("/deleteComment")
    public String deleteComment(
            @RequestParam(value = "postIdx", required = false) String postIdxStr,
            @RequestParam("cmtIdx") String cmtIdx
    ) {
        // postIdxStr이 null이 아니고, 숫자 형식일 경우에만 변환
        Long postIdx = null;  // postIdx를 Long 타입으로 선언

        if (postIdxStr != null && !postIdxStr.isEmpty()) {
            try {
                postIdx = Long.parseLong(postIdxStr);
            } catch (NumberFormatException e) {
                // postIdxStr이 숫자 형식이 아닌 경우 예외 처리 (로깅 또는 기본값 유지)
                System.out.println("Invalid postIdx format: " + postIdxStr);
            }
        }

        // cmtIdx를 Long 타입으로 변환
        Long commentIdx = Long.parseLong(cmtIdx);

        // 댓글 삭제
        commentRepository.deleteById(commentIdx);

        // 삭제 후 게시글 상세 페이지로 리다이렉트
        return "redirect:/postContent?post_idx=" + postIdx;
    }

    // 대댓글 기능
    @PostMapping("/childCmt")
    public String childCmt(
            @RequestParam(value = "postIdx", required = false) Long postIdx,
            @RequestParam(value = "parentIdx", required = false) Long parentIdx,
            @RequestParam("cmt_comment") String cmtContent,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // 세션에서 로그인 사용자 가져오기
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 안 되어 있으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // postIdx가 null인 경우 예외 처리
        if (postIdx == null) {
            // postIdx가 null이면 에러 메시지 반환
            redirectAttributes.addFlashAttribute("errorMessage", "게시글이 존재하지 않습니다.");
            return "redirect:/error";  // 적절한 에러 페이지로 리다이렉트
        }

        String userEmail = loginUser.getEmail(); // 로그인한 사용자 email

        // 대댓글 엔티티 생성
        CommentEntity reply = new CommentEntity();
        reply.setPostIdx(postIdx);
        reply.setCmt_content(cmtContent);
        reply.setParentIdx(parentIdx);  // 부모 댓글 ID (대댓글이 부모 댓글에 달리므로)
        reply.setEmail(userEmail);
        reply.setCreatedAt(LocalDateTime.now());

        // 댓글 저장
        commentRepository.save(reply);

        // 대댓글이 작성된 게시글로 리다이렉트
        redirectAttributes.addAttribute("postIdx", postIdx);
        return "redirect:/postContent?post_idx=" + postIdx;
    }

    // 댓글 기능
    @PostMapping("/sendComment")
    public String sendComment(
            @RequestParam(value = "postIdx", required = false) String postIdxStr,
            @RequestParam("cmt_comment") String cmt_content,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes // RedirectAttributes 사용
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
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        // 댓글 작성 후 Flash Attribute로 comment 전달
        redirectAttributes.addFlashAttribute("comment", comment);

        // 댓글 작성 후 해당 게시글로 리다이렉트
        return "redirect:/postContent?post_idx=" + postIdx;
    }
}
