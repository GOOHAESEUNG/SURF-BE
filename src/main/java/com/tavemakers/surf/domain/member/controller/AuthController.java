package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.LoginResDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.global.common.exception.UnauthorizedException;
import com.tavemakers.surf.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService<KakaoTokenResponseDto, KakaoUserInfoDto> kakaoAuthService;
    private final MemberRepository memberRepository;  // 👈 추가

    /**
     * 1) 카카오 인가 화면으로 리다이렉트
     */
    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String authorizeUrl = kakaoAuthService.buildAuthorizeUrl();
        response.sendRedirect(authorizeUrl);
    }

    /**
     * 2) 카카오 콜백 (Redirect URI와 동일 경로)
     *    - authCode → accessToken → userInfo 처리
     *    - refreshToken은 HttpOnly 쿠키로 전달
     */
    @GetMapping("/login/oauth2/code/kakao")
    public Mono<ApiResponse<LoginResDto>> kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        return kakaoAuthService.exchangeCodeForToken(code)
                .flatMap(token ->
                        kakaoAuthService.getUserInfo(token.accessToken())
                                .flatMap(userInfo -> {
                                    final var account = userInfo.kakaoAccount();
                                    final String email = (account != null) ? account.email() : null;

                                    if (email == null || email.isBlank()) {
                                        return Mono.error(new UnauthorizedException("카카오 계정에서 이메일 제공 동의가 필요합니다."));
                                    }

                                    // DB 조회는 블로킹 → boundedElastic 스케줄러에서 실행
                                    return Mono.fromCallable(() ->
                                                    memberRepository.findByEmailAndStatus(email, MemberStatus.APPROVED)
                                                            .orElseThrow(() -> new UnauthorizedException("관리자 승인이 필요합니다."))
                                            )
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .map(member -> {
                                                // refreshToken 쿠키 설정
                                                ResponseCookie cookie = ResponseCookie.from("refresh_token", token.refreshToken())
                                                        .httpOnly(true)
                                                        .secure(true)
                                                        .sameSite("Strict")
                                                        .path("/")
                                                        .maxAge(Duration.ofDays(14))
                                                        .build();
                                                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                                                // body에 accessToken + DB 회원 정보만 포함
                                                LoginResDto loginRes = LoginResDto.of(
                                                        token.accessToken(),
                                                        member.getName(),
                                                        member.getEmail(),
                                                        member.getProfileImageUrl()
                                                );

                                                return ApiResponse.response(HttpStatus.OK, "로그인 성공", loginRes);
                                            });
                                })
                );
    }


}
