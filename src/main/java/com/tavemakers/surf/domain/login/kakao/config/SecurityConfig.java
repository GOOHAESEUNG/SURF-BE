package com.tavemakers.surf.domain.login.kakao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 인증 없이 접근을 허용할 경로들을 배열로 정의
    private static final String[] PERMITTED_URLS = {
            // Swagger UI 접근을 위한 경로
            "/swagger-ui/**",
            "/v3/api-docs/**",
            // 카카오 로그인을 위한 경로
            "/kakao/login",
            "/login/oauth2/code/kakao"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMITTED_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .headers(h -> h.frameOptions(f -> f.disable()));
        return http.build();
    }
}