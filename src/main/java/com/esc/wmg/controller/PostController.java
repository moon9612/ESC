package com.esc.wmg.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartRequest;

import com.esc.wmg.entity.CommentEntity;
import com.esc.wmg.entity.PostEntity;
import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.repository.CommentRepository;
import com.esc.wmg.repository.PostRepository;
import com.esc.wmg.service.ImageService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {

    @Autowired
    PostRepository repository;

    @Autowired
    CommentRepository cmtRepository;

    @Autowired
    ImageService imageService;

    public PostController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 게시글 좋아요 기능
    @GetMapping("/postLikes")
    public String postLikes(
            @RequestParam("post_idx") Long postidx
    ) {

        cmtRepository.postLikes(postidx);
        return "redirect:/postContent?post_idx=" + postidx;
    }

    // 게시판 글 삭제 기능
    @GetMapping("/goPostDelete")
    public String goPostDelete(@RequestParam("post_idx") Long idx) {
        repository.deleteById(idx);
        return "redirect:/post";
    }

    // 게시판 글 수정 기능
    @PostMapping("/postUpdate")
    public String postUpdate(@RequestParam("post_idx") String postIdxStr,
            MultipartRequest request, @RequestParam(value = "existingFile", required = false) String existingFile,
            PostEntity post) throws IOException {

        // String → Long으로 변환
        Long postIdx = Long.parseLong(postIdxStr);
        // 해당 ID로 게시글 조회
        PostEntity prePost = repository.getReferenceById(postIdx);

        // 파일 업로드 처리 (S3로 전송)
        String s3Url = imageService.imageUpload(request);

        if (s3Url != null && !s3Url.isEmpty()) {
            // 새 파일이 업로드된 경우
            post.setPost_file(s3Url);
        } else {
            // 새 파일이 없으면 기존 파일 유지
            post.setPost_file(existingFile);
        }

        prePost.setUpdated_at(LocalDateTime.now());
        repository.save(post);

        return "redirect:/post";

    }

    // 게시판 글 수정 페이지 이동
    @GetMapping("goPostUpdate")
    public String goUpdate(@RequestParam("post_idx") Long idx, Model model) {
        PostEntity post = repository.getReferenceById(idx);
        model.addAttribute("post", post);
        return "PostUpdate";
    }

    // 조회수 증가 및 댓글 조회 기능
    @GetMapping("/postContent")
    public String postContent(@RequestParam("post_idx") long idx, Model model, HttpSession session) {
        repository.views(idx); // 조회수 +1

        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 게시글 가져오기
        PostEntity post = repository.findById(idx)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 댓글 목록 가져오기
        List<CommentEntity> commentList = cmtRepository.findOrderedCommentsByPostIdx(idx);

        // 모델에 담기
        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);
        model.addAttribute("loginUser", loginUser);

        return "PostContent"; // PostContent.jsp로 이동
    }

    // 게시판 글 업로드 기능
    @PostMapping("/postWriting")
    public String postWriting(PostEntity post, MultipartRequest request) throws Exception {
        // 파일 업로드 처리 (S3로 전송)
        String s3Url = imageService.imageUpload(request);
        post.setPost_file(s3Url);

        // 게시글 저장
        repository.save(post);
        // 게시글 작성 완료 후 게시판 페이지로 리디렉션
        return "redirect:/post"; // 게시글 목록 페이지로 리디렉션
    }

    // 게시판 글작성 페이지 이동
    @GetMapping("/postWrite")
    public String boardWrite(HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }
        return "postWrite";
    }

    @GetMapping("/post")
    public String post(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchPost", required = false) String searchPost,
            @RequestParam(value = "category", defaultValue = "all") String category,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<PostEntity> postPage;

        if (searchPost != null && !searchPost.isEmpty() && !"all".equals(category)) {
            postPage = repository.findByPostTitleContainingAndPostCategory(searchPost, category, pageable);
        } else if (searchPost != null && !searchPost.isEmpty()) {
            postPage = repository.findByPostTitleContaining(searchPost, pageable);
        } else if (!"all".equals(category)) {
            postPage = repository.findByPostCategory(category, pageable);
        } else {
            postPage = repository.findAll(pageable);
        }

        // 1) 게시글 목록
        List<PostEntity> posts = postPage.getContent();
        model.addAttribute("post_list", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("category", category);

        return "post";
    }

}
