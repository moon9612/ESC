package com.esc.wmg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers("/postContent/**", "/postWrite/**").authenticated() // 인증이 필요한 경로
                .anyRequest().permitAll() // 나머지 경로는 모두 허용
            )
            .formLogin(form -> form
                .loginPage("/login") // 로그인 페이지 지정
                .permitAll()
                .defaultSuccessUrl("/", true) // 로그인 성공 시 홈 페이지로 리다이렉트
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login"); // 인증되지 않은 경우 로그인 페이지로 리다이렉트
                })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
