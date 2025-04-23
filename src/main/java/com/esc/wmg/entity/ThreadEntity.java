package com.esc.wmg.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "tbl_thread")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ThreadEntity {

    // pk - thread_id (채팅방 ID)
    @Id
    @NonNull
    @Column(name = "thread_id", nullable = false, length = 50)
    private String threadId;

    // fk - 사용자 이메일 (tbl_user 참조)
    @NonNull
    @JoinColumn(name = "email",  nullable = false)
    private String email;

    // 방 제목
    @Column(name = "room_title", length = 500)
    private String roomTitle;

    // 방 소개글
    @Column(name = "room_info", columnDefinition = "TEXT")
    private String roomInfo;

    // 최대 인원 수
    @Column(name = "room_limit")
    private Integer room_limit;

    // 방 상태 (예: "active", "closed" 등)
    @Column(name = "room_status", length = 50)
    private String room_status;

    // 키워드 (검색용)
    @Column(name = "room_keyword", columnDefinition = "TEXT")
    private String room_keyword;

    // thread 생성 시간
    @CreationTimestamp
    @Column(name = "thread_at", updatable = false)
    private LocalDateTime threadAt;

}
