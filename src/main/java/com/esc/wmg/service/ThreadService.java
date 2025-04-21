package com.esc.wmg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esc.wmg.entity.ChatEntity;
import com.esc.wmg.entity.ThreadEntity;
import com.esc.wmg.repository.ChatRepository;
import com.esc.wmg.repository.ThreadRepository;

@Service
public class ThreadService {

    @Autowired
    private ThreadRepository threadRepository;
    private ChatRepository chatRepository;

    // thread_id 저장
    public void saveThreadId(ThreadEntity thread) {
        if (thread == null) {
            throw new IllegalArgumentException("thread is null");
        }
        threadRepository.save(thread);
    }   

    // 이메일로 ThreadEntity 전체 조회
    public List<ThreadEntity> getAllThreadByEmail(String email) {
        return threadRepository.findAllByEmail(email);
    }

    public List<ChatEntity> getChatByThreadId(String thread_id) {
        // TODO Auto-generated method stub
        return chatRepository.findAllByThreadId(thread_id);
    }

}