package com.esc.wmg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esc.wmg.entity.ChatEntity;
import com.esc.wmg.entity.ThreadEntity;
import com.esc.wmg.repository.ChatRepository;
import com.esc.wmg.repository.ThreadRepository;

import jakarta.transaction.Transactional;

@Service
public class ThreadService {

    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private ChatRepository chatRepository;

    // threadId 저장
    @Transactional
    public void saveThreadId(ThreadEntity thread) {
        if (thread == null) {
            throw new IllegalArgumentException("thread is null");
        }
        threadRepository.save(thread);
    }   

    // 이메일로 ThreadEntity 전체 조회 (최신순)
    @Transactional
    public List<ThreadEntity> getAllThreadByEmail(String email) {
        return threadRepository.findAllByEmailOrderByThreadAtDesc(email);
    }

    // threadId로 ChatEntity 조회
    @Transactional
    public List<ChatEntity> getChatByThreadId(String threadId) {
        return chatRepository.findAllByThreadId(threadId);
    }
}