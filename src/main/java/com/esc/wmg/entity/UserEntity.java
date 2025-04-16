package com.esc.wmg.entity;

import java.time.LocalDate;

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
    @Column(length = 50)
    private String email;
    @NonNull
    @Column(length = 50)
    private String nick;
    @NonNull
    @Column(length = 50)
    private String pw;
    @NonNull
    @Column
    private LocalDate birthDate;
    
}
