package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.LoginResDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.member.service.MemberUpsertService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.jwt.JwtService;
import com.tavemakers.surf.global.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService<KakaoTokenResponseDto, KakaoUserInfoDto> kakaoAuthService;
    private final JwtService jwtService;
    private final MemberUpsertService memberUpsertService;

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
