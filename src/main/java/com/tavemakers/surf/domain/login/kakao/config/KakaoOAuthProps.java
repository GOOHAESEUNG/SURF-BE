package com.tavemakers.surf.domain.login.kakao.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Getter
@ConfigurationProperties(prefix = "kakao")
@Validated
public class KakaoOAuthProps {
    @NotBlank private final String clientId;
    private final String clientSecret; // optional
    @NotBlank private final String redirectUri;

    public KakaoOAuthProps(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }
}
