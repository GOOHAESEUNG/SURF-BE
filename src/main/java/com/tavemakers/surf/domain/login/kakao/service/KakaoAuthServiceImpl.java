package com.tavemakers.surf.domain.login.kakao.service;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.kakao.config.KakaoOAuthProps;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.BodyInserters;

@Service("kakaoAuthService") // 다른 구현체들과 구분하기 위해 명시적 이름 부여
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements AuthService<KakaoTokenResponseDto> {

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
        return kakaoAuthWebClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", props.getClientId())
                        .with("redirect_uri", props.getRedirectUri())
                        .with("code", code)
                        .with("client_secret", props.getClientSecret()))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("카카오 인증 요청 오류: " + errorBody))))
                .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("카카오 서버 오류: " + errorBody))))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();
    }

    @Override
    public String getAccessTokenInfo(String accessToken) {
        return kakaoApiWebClient.get()
                .uri("/v1/user/access_token_info")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("잘못된 AccessToken: " + errorBody))))
                .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("카카오 서버 오류: " + errorBody))))
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public String getUserInfo(String accessToken) {
        return kakaoApiWebClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("카카오 사용자 정보 요청 오류: " + errorBody))))
                .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("카카오 서버 오류: " + errorBody))))
                .bodyToMono(String.class) // JSON 그대로 반환 (추후 DTO로 매핑 가능)
                .block();
    }

}
