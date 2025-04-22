// package com.esc.wmg.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/postWrite", "/postContent").authenticated() // 로그인 필요 경로
//                 .anyRequest().permitAll() // 나머지는 허용
//             )
//             .formLogin(form -> form
//                 .loginPage("/login") // 로그인 페이지 경로
//                 .defaultSuccessUrl("/", true) // 로그인 성공 시 메인 페이지로 리디렉션
//                 .permitAll()
//             )
//             .logout(logout -> logout
//                 .logoutSuccessUrl("/login?logout") // 로그아웃 후 로그인 페이지로 이동
//                 .permitAll()
//             );

//         return http.build();
//     }
// }
