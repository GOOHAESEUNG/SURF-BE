package com.tavemakers.surf.domain.login.kakao.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "kakao")
public class KakaoOAuthProps {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public KakaoOAuthProps(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }
}
