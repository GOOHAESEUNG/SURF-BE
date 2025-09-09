package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.login.AuthService;
import com.tavemakers.surf.domain.kakao.dto.KakaoTokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    // 인터페이스 타입으로 주입
    private final AuthService<KakaoTokenResponseDto> kakaoAuthService;

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
     */
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<KakaoTokenResponseDto> kakaoCallback(
            @RequestParam("code") String code
    ) {
        KakaoTokenResponseDto token = kakaoAuthService.exchangeCodeForToken(code);
        return ResponseEntity.ok(token);
    }

    /**
     * 3) 카카오 Access Token 유효성 확인
     */
    @GetMapping("/login/kakao/token-info")
    public ResponseEntity<String> kakaoTokenInfo(@RequestParam("access_token") String accessToken) {
        String info = kakaoAuthService.getAccessTokenInfo(accessToken);
        return ResponseEntity.ok(info);
    }

    /**
     * 4) 카카오 사용자 정보 조회
     */
    @GetMapping("/login/kakao/user-info")
    public ResponseEntity<String> kakaoUserInfo(@RequestParam("access_token") String accessToken) {
        String userInfo = (String) kakaoAuthService.getUserInfo(accessToken);
        return ResponseEntity.ok(userInfo);
    }
}
