package com.esc.wmg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 모든 경로를 허용 (이 설정은 필요에 따라 조정할 수 있음)
            )
            .formLogin(form -> form
                .loginPage("/login") // 로그인 페이지 지정
                .permitAll()
                .defaultSuccessUrl("/", true) // 로그인 성공 시 홈 페이지로 리다이렉트
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃 URL 설정 (기본값: /logout)
                .logoutSuccessUrl("/") // 로그아웃 후 리다이렉트할 URL 설정
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // 쿠키 삭제
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
