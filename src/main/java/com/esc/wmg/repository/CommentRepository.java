package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esc.wmg.entity.CommentEntity;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // 게시글(postIdx)에 해당하는 모든 댓글 조회
    List<CommentEntity> findByPostIdx(long postIdx);

    // 게시글(postIdx)에 해당하는 댓글을 created_at 기준 내림차순으로 조회
    List<CommentEntity> findByPostIdxOrderByCreatedAtDesc(Long postIdx);

    // postIdx에 해당하는 댓글을 조회하고, parentIdx가 null인 경우 부모 댓글을 먼저, 그 이후에는 댓글 작성 시간을 기준으로 오름차순 정렬
    @Query("SELECT c FROM CommentEntity c WHERE c.postIdx = :postIdx ORDER BY COALESCE(c.parentIdx, c.cmtIdx), c.createdAt ASC")
    List<CommentEntity> findOrderedCommentsByPostIdx(@Param("postIdx") Long postIdx);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.post_likes = p.post_likes+1 where p.post_idx = :idx")
    void postLikes(@Param("idx") long idx);

}
