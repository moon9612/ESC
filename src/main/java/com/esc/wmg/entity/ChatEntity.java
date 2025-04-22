package com.esc.wmg.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "tbl_chat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ChatEntity {
    //채팅 하나의 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chat_idx;

    @NonNull
    @Column(name = "thread_id", nullable = false)
    /**
     * thread_id를 저장하는 컬럼
     * thread_id가 같은 채팅 메시지들은 같은 쓰레드에 속함
     * 따라서 thread_id를 통해 같은 쓰레드에 속하는 메시지들을 그룹화할 수 있음
     */
    private String threadId;

    @NonNull
    @Column(name = "chatter")
    /**
     * chatter는 채팅을 하는 사람(email)과 AI(bot)를 구분하기 위한 컬럼
     * 이 컬럼을 통해 누가 채팅을 했는지 알 수 있음
     */
    private String chatter;

    @NonNull
    @Column(name = "chat_content")
    /**
     * chat_content는 채팅 메시지의 내용을 저장하는 컬럼
     * 이 컬럼을 통해 실제 채팅 내용을 알 수 있음
     */
    private String chat_content;


    @Column(name = "cont_keyword")
    /**
     * 사용 미정
     * cont_keyword는 메시지의 키워드를 저장하는 컬럼
     * 이 컬럼을 통해 메시지의 주제를 알 수 있음
     */
    private String cont_keyword;


    @Column(name = "chat_emoticon")
    /**
     * 사용 미정
     * chat_emoticon은 메시지에 사용된 이모티콘을 저장하는 컬럼
     */
    private String chat_emoticon;


    @Column(name = "chat_file")
    /**
     * 사용 미정
     * chat_file은 메시지에 첨부된 파일을 저장하는 컬럼
     * 이 컬럼을 통해 메시지에 첨부된 파일의 경로를 알 수 있음
     */
    private String chat_file;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    /**
     * created_at은 채팅 메시지가 생성된 시간을 저장하는 컬럼
     * 이 컬럼을 통해 채팅 메시지가 언제 생성되었는지 알 수 있음
     */
    private LocalDateTime created_at;


}
