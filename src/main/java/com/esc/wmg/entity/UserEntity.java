package com.esc.wmg.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "tbl_user") // 기존 테이블명과 정확히 일치
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {

    @Id
    @NonNull
    @Column(length = 50, name = "email")
    private String email;
    
    @NonNull
    @Column(length = 50 ,name = "nick")
    private String nick;
    
    @NonNull
    @Column(length = 50, name = "pw")
    private String pw;
    
    @NonNull
    @Column(name = "birthdate")
    private LocalDate birthDate;
    
    @NonNull
    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
}
