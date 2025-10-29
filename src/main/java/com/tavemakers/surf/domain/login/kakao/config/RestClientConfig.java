package com.tavemakers.surf.domain.login.kakao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean(name = "kakaoAuthRestTemplate")
    public RestTemplate kakaoAuthRestTemplate() {
        return new RestTemplate(); // 카카오 인증 서버 요청용
    }

    @Bean(name = "kakaoApiRestTemplate")
    public RestTemplate kakaoApiRestTemplate() {
        return new RestTemplate(); // 카카오 사용자 정보 요청용
    }
}