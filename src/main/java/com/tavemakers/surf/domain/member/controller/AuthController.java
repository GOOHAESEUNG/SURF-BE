package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.LoginResDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.service.MemberUpsertService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "소셜 로그인 관련", description = "이 명세서를 통해서는 아무런 결과는 안나옵니다..")
public class AuthController {

    private final AuthService<KakaoTokenResponseDto, KakaoUserInfoDto> kakaoAuthService;
    private final JwtService jwtService;
    private final MemberUpsertService memberUpsertService;

    /**
     * 1) 카카오 인가 화면으로 리다이렉트
     */
    @Operation(summary = "카카오 인가 화면으로 리다이렉트")
    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String requestId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();

        String authorizeUrl = kakaoAuthService.buildAuthorizeUrl();

        log.info("timestamp={}, event_type=INFO, log_event=login.kakao.request, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                java.time.Instant.now(),
                null,
                "/login/kakao",
                "카카오 로그인 인가 요청",
                requestId,
                "REGISTERING",
                "GET",
                "/login/kakao",
                200,
                System.currentTimeMillis() - start,
                "success",
                Map.of(
                        "login_method", "kakao",
                        "redirect_uri", authorizeUrl
                )
        );
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 2) 카카오 콜백 (Redirect URI와 동일 경로)
     *    - authCode → accessToken → userInfo 처리
     *    - refreshToken은 HttpOnly 쿠키로 전달
     */
    @Operation(
            summary = "카카오 로그인 콜백",
            description = "인가 코드(code)를 받아 JWT AccessToken과 사용자 정보를 반환합니다.")
    @GetMapping("/login/oauth2/code/kakao")
    public Mono<ApiResponse<LoginResDto>> kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        String requestId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();

        // 콜백 수신 로그
        log.info("timestamp={}, event_type=INFO, log_event=login.kakao.callback, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                java.time.Instant.now(),
                null,
                "/login/oauth2/code/kakao",
                "카카오 콜백 수신",
                requestId,
                "REGISTERING",
                "GET",
                "/login/oauth2/code/kakao",
                200,
                System.currentTimeMillis() - start,
                "success",
                Map.of("provider", "kakao", "code_length", code.length())
        );
        return kakaoAuthService.exchangeCodeForToken(code, requestId)
                .flatMap(token ->
                        kakaoAuthService.getUserInfo(token.accessToken(), requestId)
                                .map(userInfo -> {
                                    // 1) 회원 upsert: 없으면 REGISTERING 상태로 생성
                                    Member member = memberUpsertService.upsertRegisteringFromKakao(userInfo);

                                    // 2) 우리 JWT 발급 (subject=memberId, role 포함)
                                    String accessToken  = jwtService.createAccessToken(member.getId(), member.getRole().name());
                                    String refreshToken = jwtService.createRefreshToken(member.getId());

                                    // 3) 헤더/쿠키로 토큰 전송 (refresh는 HttpOnly 쿠키)
                                    jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

                                    var account = userInfo.kakaoAccount();
                                    String email = account.email();
                                    String nickname = account.profile().nickname();
                                    String profileImageUrl = account.profile().profileImageUrl();

                                    // 로그인 성공 로그 (login.succeeded)
                                    long duration = System.currentTimeMillis() - start;
                                    log.info("timestamp={}, event_type=INFO, log_event=login.succeeded, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                                            java.time.Instant.now(),
                                            member.getId(),
                                            "/login/oauth2/code/kakao",
                                            "로그인 성공",
                                            requestId,
                                            "REGISTERING",
                                            "POST",
                                            "/login/oauth2/code/kakao",
                                            200,
                                            duration,
                                            "success",
                                            Map.of(
                                                    "provider", "kakao",
                                                    "user_id", member.getId(),
                                                    "nickname", nickname,
                                                    "email_domain", email.substring(email.indexOf("@") + 1),
                                                    "token_prefix", accessToken.substring(0, 8) + "..."
                                            )
                                    );

                                    // 4) 바디에는 accessToken + 프로필
                                    LoginResDto loginRes = LoginResDto.of(
                                            accessToken,
                                            nickname,
                                            email,
                                            profileImageUrl
                                    );
                                    return ApiResponse.response(HttpStatus.OK, "로그인 성공", loginRes);
                                })
                );
    }


}
