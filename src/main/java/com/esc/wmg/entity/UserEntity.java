package com.esc.wmg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
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
    @Column(length = 10)
    private int age;
    @Column(length = 50)
    private String industry;
    @Column(length = 50)
    private String job_type;
    @Column(length = 50)
    private String company_size;
    @Column(length = 50)
    private int work_time;
}
