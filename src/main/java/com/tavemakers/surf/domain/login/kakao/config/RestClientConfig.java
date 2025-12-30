package com.tavemakers.surf.domain.login.kakao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean(name = "kakaoAuthRestTemplate")
    public RestTemplate kakaoAuthRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(5))
                .build(); // 카카오 인증 서버 요청용
    }

    @Bean(name = "kakaoApiRestTemplate")
    public RestTemplate kakaoApiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(5))
                .build(); // 카카오 사용자 정보 요청용
   }
}