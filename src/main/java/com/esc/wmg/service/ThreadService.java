package com.esc.wmg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esc.wmg.entity.ThreadEntity;
import com.esc.wmg.repository.ThreadRepository;

@Service
public class ThreadService {

    @Autowired
    private ThreadRepository threadRepository;

    // 이메일로 thread_id 조회
    public String getThreadIdByEmail(String email) {
        ThreadEntity thread = threadRepository.findByUser_Email(email);
        if (thread != null) {
            return thread.getThreadId();
        }
        return null; // 해당 이메일에 대한 thread_id가 없을 경우
    }

    // thread_id 저장
    public void saveThreadId(ThreadEntity thread) {
        if (thread == null) {
            throw new IllegalArgumentException("thread is null");
        }
        threadRepository.save(thread);
    }
}