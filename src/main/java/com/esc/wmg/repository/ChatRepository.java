package com.esc.wmg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.ChatEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long>{
    // thread_id로 채팅 내역 전체 조회
    List<ChatEntity> findAllByThreadId(String thread_id);
}

