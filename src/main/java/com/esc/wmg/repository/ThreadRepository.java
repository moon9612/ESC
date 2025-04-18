package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.ThreadEntity;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadEntity, String> {

    // 이메일로 ThreadEntity 조회
    ThreadEntity findByUser_Email(String email);
}