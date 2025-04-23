package com.esc.wmg.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esc.wmg.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // Pageable 객체를 이용한 페이징 처리
    Page<PostEntity> findAll(Pageable pageable);

    // 제목과 카테고리를 기준으로 게시글 조회
    @Query("SELECT p FROM PostEntity p WHERE p.post_title LIKE %:postTitle% AND p.post_category = :postCategory")
    Page<PostEntity> findByPostTitleContainingAndPostCategory(@Param("postTitle") String postTitle,
            @Param("postCategory") String postCategory,
            Pageable pageable);

    // 제목을 기준으로 게시글 조회
    @Query("SELECT p FROM PostEntity p WHERE p.post_title LIKE %:postTitle%")
    Page<PostEntity> findByPostTitleContaining(@Param("postTitle") String postTitle, Pageable pageable);

    // 카테고리를 기준으로 게시글 조회
    @Query("SELECT p FROM PostEntity p WHERE p.post_category = :postCategory")
    Page<PostEntity> findByPostCategory(@Param("postCategory") String postCategory, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.post_views = p.post_views+1 where p.post_idx = :idx")
    void views(@Param("idx") long idx);

}
