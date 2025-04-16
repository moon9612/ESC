package com.esc.wmg.model;

import java.time.LocalDate;

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
    private LocalDate birthDate;
    
}
