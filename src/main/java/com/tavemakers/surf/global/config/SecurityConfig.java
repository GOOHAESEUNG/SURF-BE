package com.tavemakers.surf.global.config;

import com.tavemakers.surf.domain.member.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 전역 설정
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    // 인증 없이 접근 가능한 URL 정의
    private static final String[] PERMITTED_URLS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/kakao/login",
            "/login/oauth2/code/kakao",
            "/login/**",
            "/api/members/signup"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // JWT 사용 시 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMITTED_URLS).permitAll() // 허용 URL
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 접근
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()) // 우리는 소셜 로그인 + JWT 사용 → formLogin 비활성화
                .httpBasic(basic -> basic.disable()) // Basic Auth 비활성화
                .headers(h -> h.frameOptions(f -> f.disable())); // H2 console 접근 허용 필요 시

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
