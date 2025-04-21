package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.ThreadEntity;

import java.util.List;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadEntity, String> {

    // 이메일로 ThreadEntity 조회
    // ThreadEntity findByEmail(String email);

    // 이메일로 ThreadEntity 전체 조회
    List<ThreadEntity> findAllByEmail(String email);
}