package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esc.wmg.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>{
    
    @Transactional
    @Modifying
    @Query("update PostEntity p set p.post_views = p.post_views+1 where p.post_idx = :idx")
    void views(@Param("idx") long idx);
}
