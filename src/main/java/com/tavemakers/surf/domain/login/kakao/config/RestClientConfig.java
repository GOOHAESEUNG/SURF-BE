package com.tavemakers.surf.domain.login.kakao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientConfig {

    @Bean(name = "kakaoAuthWebClient")
    public WebClient kakaoAuthWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://kauth.kakao.com").build(); // 토큰 발급 서버
    }

    @Bean(name = "kakaoApiWebClient")
    public WebClient kakaoApiWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://kapi.kakao.com").build(); // API 서버
    }
}
