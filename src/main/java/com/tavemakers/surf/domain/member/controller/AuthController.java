package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.login.LoginResDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.service.MemberUpsertService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.jwt.JwtService;
import com.tavemakers.surf.global.logging.LogParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

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
     * event: login.kakao.request
     */
    @Operation(summary = "카카오 인가 화면으로 리다이렉트")
    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String authorizeUrl = kakaoAuthService.buildAuthorizeUrl();

        String redirectUri = UriComponentsBuilder.fromUriString(authorizeUrl)
                .build()
                .getQueryParams()
                .getFirst("redirect_uri");

        kakaoAuthService.logAuthorize("kakao", redirectUri);

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", authorizeUrl);
    }

    /**
     * 2) 카카오 콜백 (Redirect URI와 동일 경로)
     *    - authCode → accessToken → userInfo 처리
     *    - refreshToken은 HttpOnly 쿠키로 전달
     * event: login.kakao.callback
     */
    @Operation(
            summary = "카카오 로그인 콜백",
            description = "인가 코드(code)를 받아 JWT AccessToken과 사용자 정보를 반환합니다."
    )
    @GetMapping("/login/oauth2/code/kakao")
    public ApiResponse<LoginResDto> kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        if (code == null || code.isBlank()) {
            return ApiResponse.response(HttpStatus.BAD_REQUEST, "인가 코드가 없습니다.", null);
        }
        try {
            kakaoAuthService.logCallback("kakao", code.length());

            // 토큰 발급
            KakaoTokenResponseDto token = kakaoAuthService.exchangeCodeForToken(code);

            // 사용자 정보 조회
            KakaoUserInfoDto userInfo = kakaoAuthService.getUserInfo(token.accessToken());

            // 회원 upsert
            Member member = memberUpsertService.upsertRegisteringFromKakao(userInfo);

            // JWT 발급
            String accessToken = jwtService.createAccessToken(member.getId(), member.getRole().name());
            String refreshToken = jwtService.createRefreshToken(member.getId());
            jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    member.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(member.getRole().name()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            kakaoAuthService.logLoginSuccess(member.getId(), accessToken.substring(0, 10) + "...");

            var account = userInfo.kakaoAccount();
            LoginResDto loginRes = LoginResDto.of(
                    accessToken,
                    account.profile().nickname(),
                    account.email(),
                    account.profile().profileImageUrl()
            );

            return ApiResponse.response(HttpStatus.OK, "로그인 성공", loginRes);

        } catch (Exception e) {
            try {
                kakaoAuthService.logLoginFailed(
                        401,
                        e.getMessage() != null ? e.getMessage() : "카카오 로그인 실패"
                );
            } catch (Exception ignored) {
                // AOP가 ERROR로 인식하도록 예외는 던지되, 여기서 무시함
            }

            return ApiResponse.response(HttpStatus.UNAUTHORIZED, "로그인 실패", null);
        }

    }
}