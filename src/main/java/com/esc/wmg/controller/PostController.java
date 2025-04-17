package com.esc.wmg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esc.wmg.entity.PostEntity;
import com.esc.wmg.repository.PostRepository;

@Controller
public class PostController {

    @Autowired
    PostRepository repository;


    @GetMapping("/postContent")
    public String postContent(@RequestParam("post_idx") long idx, Model model) {
    
        repository.views(idx); // ✅ 먼저 조회수 +1
    
        PostEntity post = repository.findById(idx)
                                    .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    
        model.addAttribute("post", post);
        return "PostContent";
    }
    


    // 게시판 글 업로드 기능
    @PostMapping("/postWriting")
    public String boardWriting(PostEntity post) {
        if (post == null) {
            throw new IllegalArgumentException("post is null");
        }
        repository.save(post); 
        return "redirect:/post";
    }

    // 게시판 글작성 페이지 이동
    @GetMapping("/postWrite")
    public String boardWrite() {
        return "postWrite";
    }

    // 게시판 글 조회
    @GetMapping("/post")
    public String post(Model model) {

        List<PostEntity> post_list = repository.findAll();
        model.addAttribute("post_list", post_list);

        return "post";
    }

}
