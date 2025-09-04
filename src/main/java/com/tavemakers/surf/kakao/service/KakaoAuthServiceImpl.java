package com.tavemakers.surf.kakao.service;

import com.tavemakers.surf.kakao.config.KakaoOAuthProps;
import com.tavemakers.surf.kakao.dto.KakaoTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService {

    private final @Qualifier("kakaoAuthWebClient") WebClient kakaoAuthWebClient;
    private final @Qualifier("kakaoApiWebClient")  WebClient kakaoApiWebClient;
    private final KakaoOAuthProps props;

    @Override
    public String buildAuthorizeUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + props.getClientId()
                + "&redirect_uri=" + props.getRedirectUri()
                + "&scope=account_email"; // 필요 시 확장
    }

    @Override
    public KakaoTokenResponseDto exchangeCodeForToken(String code) {
        String body = "grant_type=authorization_code"
                + "&client_id=" + props.getClientId()
                + "&redirect_uri=" + props.getRedirectUri()
                + "&code=" + code
                + (props.getClientSecret() == null || props.getClientSecret().isBlank()
                ? "" : "&client_secret=" + props.getClientSecret());

        return kakaoAuthWebClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();
    }

    @Override
    public String getAccessTokenInfo(String accessToken) {
        return kakaoApiWebClient.get()
                .uri("/v1/user/access_token_info")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
