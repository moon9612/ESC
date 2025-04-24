package com.esc.wmg.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class MailEntity {

    @Id
    private Long id;  // 식별자 필드 추가
    private String Subject;
    private String message;

}
