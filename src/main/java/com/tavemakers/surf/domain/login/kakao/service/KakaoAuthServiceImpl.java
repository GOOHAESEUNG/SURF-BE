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
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
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
    public Mono<KakaoTokenResponseDto> exchangeCodeForToken(String code, String requestId) {

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
                        resp -> handleError(resp, "카카오 인증 요청 오류", requestId))
                .onStatus(s -> s.is5xxServerError(),
                        resp -> handleError(resp, "카카오 서버 오류", requestId))
                .bodyToMono(KakaoTokenResponseDto.class);
    }

    @Override
    public Mono<Map<String, Object>> getAccessTokenInfo(String accessToken, String requestId) {

        return kakaoApiWebClient.get()
                .uri("/v1/user/access_token_info")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        resp -> handleError(resp, "잘못된 AccessToken", requestId ))
                .onStatus(s -> s.is5xxServerError(),
                        resp -> handleError(resp, "카카오 서버 오류", requestId))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    @Override
    public Mono<KakaoUserInfoDto> getUserInfo(String accessToken, String requestId) {
        return kakaoApiWebClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        resp -> handleError(resp, "카카오 사용자 정보 요청 오류", requestId))
                .onStatus(s -> s.is5xxServerError(),
                        resp -> handleError(resp, "카카오 서버 오류", requestId))
                .bodyToMono(KakaoUserInfoDto.class);
    }

    /**
     * 공통 에러 처리 메서드
     */
    private Mono<Throwable> handleError(ClientResponse resp, String message, String requestId) {
        long start = System.currentTimeMillis();

        return resp.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    long duration = System.currentTimeMillis() - start;

                    // login.failed 로그 추가
                    log.error("timestamp={}, event_type=ERROR, log_event=login.failed, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                            java.time.Instant.now(),
                            null,
                            "/login/kakao/error",
                            message,
                            requestId,
                            "MEMBER",
                            "POST",
                            resp.request().getURI().getPath(),
                            resp.statusCode().value(),
                            duration,
                            "fail",
                            Map.of(
                                    "error_code", resp.statusCode().value(),
                                    "error_msg", message,
                                    "provider", "kakao"
                            )
                    );

                    return Mono.error(new org.springframework.web.server.ResponseStatusException(
                            resp.statusCode(),
                            message + ": " + errorBody
                    ));
                });
    }
}
