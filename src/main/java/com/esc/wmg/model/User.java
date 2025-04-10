package com.esc.wmg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    private String email;
    private String nick;
    private String pw;
    private int age;
    private String industry;
    private String job_type;
    private String company_size;
    private int work_time;
    
}
