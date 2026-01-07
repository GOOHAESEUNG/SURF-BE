package com.tavemakers.surf.domain.login.kakao.service;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.kakao.config.KakaoOAuthProps;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements AuthService<KakaoTokenResponseDto, KakaoUserInfoDto> {

    private final @Qualifier("kakaoAuthRestTemplate") RestTemplate kakaoAuthRestTemplate;
    private final @Qualifier("kakaoApiRestTemplate")  RestTemplate kakaoApiRestTemplate;
    private final KakaoOAuthProps props;

    @Override
    public String buildAuthorizeUrl(String redirectUri) {

        logAuthorize("kakao", redirectUri);

        return UriComponentsBuilder
                .fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", props.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "account_email profile_nickname profile_image")
                .build()
                .toUriString();
    }

    /** 인가 코드 → 토큰 교환 */
    @Override
    public KakaoTokenResponseDto exchangeCodeForToken(String code) {
        try {
            String url = "https://kauth.kakao.com/oauth/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", props.getClientId());
            params.add("redirect_uri", props.getRedirectUri());
            params.add("code", code);

            log.debug("[KAKAO][TOKEN] params={}", params);

            if (props.getClientSecret() != null && !props.getClientSecret().isBlank()) {
                params.add("client_secret", props.getClientSecret());
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<KakaoTokenResponseDto> response =
                    kakaoAuthRestTemplate.postForEntity(url, request, KakaoTokenResponseDto.class);

            KakaoTokenResponseDto body = java.util.Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty token response from Kakao"));
            return body;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw handleError(e, HttpStatus.BAD_REQUEST, "카카오 인증 요청 오류");
        } catch (Exception e) {
            throw handleError(e, HttpStatus.INTERNAL_SERVER_ERROR, "카카오 인증 요청 실패");
        }
    }

    /** AccessToken으로 사용자 정보 요청 */
    @Override
    public KakaoUserInfoDto getUserInfo(String accessToken) {
        try {
            String url = "https://kapi.kakao.com/v2/user/me";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfoDto> response = kakaoApiRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    KakaoUserInfoDto.class
            );

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw handleError(e, HttpStatus.BAD_REQUEST, "카카오 사용자 정보 요청 오류");
        } catch (Exception e) {
            throw handleError(e, HttpStatus.INTERNAL_SERVER_ERROR, "카카오 사용자 정보 요청 실패");
        }
    }

    /** AccessToken 유효성 검증 */
    @Override
    public Map<String, Object> getAccessTokenInfo(String accessToken) {
        try {
            String url = "https://kapi.kakao.com/v1/user/access_token_info";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = kakaoApiRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw handleError(e, HttpStatus.BAD_REQUEST, "AccessToken 검증 실패");
        } catch (Exception e) {
            throw handleError(e, HttpStatus.INTERNAL_SERVER_ERROR, "AccessToken 검증 중 서버 오류");
        }
    }

    /** 로그인 관련 공통 에러 처리 및 로그 기록 */
    protected ResponseStatusException handleError(Exception ex, HttpStatus status, String message) {
        log.error("{}: {}", message, ex.getMessage(), ex);
        return new ResponseStatusException(status, message, ex);
    }

    // 카카오 인가 요청 로그
    @Override
    @LogEvent("login.kakao.request")
    public void logAuthorize(
            @LogParam("login_method") String loginMethod,
            @LogParam("redirect_uri") String redirectUri
    ) {}

    // 카카오 콜백 로그
    @Override
    @LogEvent("login.kakao.callback")
    public void logCallback(
            @LogParam("provider") String provider,
            @LogParam("code_length") int codeLength
    ) {}

    // 로그인 성공 로그
    @Override
    @LogEvent("login.succeeded")
    public void logLoginSuccess(
            @LogParam("user_id") Long userId,
            @LogParam("issued_token") String issuedToken
    ) {}

    // 로그인 실패 로그
    @Override
    @LogEvent("login.failed")
    public void logLoginFailed(
            @LogParam("error_code") int errorCode,
            @LogParam("error_msg") String errorMsg
    ) {
        throw new IllegalStateException(errorMsg);
    }
}