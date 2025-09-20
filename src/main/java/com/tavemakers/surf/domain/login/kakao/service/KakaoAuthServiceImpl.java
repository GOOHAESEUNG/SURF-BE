package com.tavemakers.surf.domain.login.kakao.service;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.kakao.config.KakaoOAuthProps;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements AuthService<KakaoTokenResponseDto, KakaoUserInfoDto> {

    private final @Qualifier("kakaoAuthWebClient") WebClient kakaoAuthWebClient;
    private final @Qualifier("kakaoApiWebClient")  WebClient kakaoApiWebClient;
    private final KakaoOAuthProps props;

    @Override
    public String buildAuthorizeUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + props.getClientId()
                + "&redirect_uri=" + props.getRedirectUri()
                + "&scope=account_email profile_nickname profile_image";

}

    @Override
    public Mono<KakaoTokenResponseDto> exchangeCodeForToken(String code) {
        var form = BodyInserters
                .fromFormData("grant_type", "authorization_code")
                .with("client_id", props.getClientId())
                .with("redirect_uri", props.getRedirectUri())
                .with("code", code);

        var secret = props.getClientSecret();
        if (secret != null && !secret.isBlank()) {
            form = form.with("client_secret", secret);
        }

        return kakaoAuthWebClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        resp -> handleError(resp, "카카오 인증 요청 오류"))
                .onStatus(s -> s.is5xxServerError(),
                        resp -> handleError(resp, "카카오 서버 오류"))
                .bodyToMono(KakaoTokenResponseDto.class);
    }

    @Override
    public Mono<Map<String, Object>> getAccessTokenInfo(String accessToken) {
        return kakaoApiWebClient.get()
                .uri("/v1/user/access_token_info")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        resp -> handleError(resp, "잘못된 AccessToken"))
                .onStatus(s -> s.is5xxServerError(),
                        resp -> handleError(resp, "카카오 서버 오류"))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    @Override
    public Mono<KakaoUserInfoDto> getUserInfo(String accessToken) {
        return kakaoApiWebClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        resp -> handleError(resp, "카카오 사용자 정보 요청 오류"))
                .onStatus(s -> s.is5xxServerError(),
                        resp -> handleError(resp, "카카오 서버 오류"))
                .bodyToMono(KakaoUserInfoDto.class);
    }

    /**
     * 공통 에러 처리 메서드
     */
    private Mono<Throwable> handleError(ClientResponse resp, String message) {
        return resp.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(
                        new org.springframework.web.server.ResponseStatusException(
                                resp.statusCode(), message + ": " + errorBody)));
    }
}
