package com.tavemakers.surf.kakao.controller;

import com.tavemakers.surf.kakao.dto.KakaoTokenResponseDto;
import com.tavemakers.surf.kakao.service.KakaoAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    // 1) 카카오 인가 화면으로 이동
    @GetMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        // AuthService에서 URL을 만들어 주는 게 더 깔끔 (buildAuthorizeUrl())
        String authorizeUrl = kakaoAuthService.buildAuthorizeUrl();
        response.sendRedirect(authorizeUrl);
    }

    // 2) 카카오 콜백 (Redirect URI와 동일 경로)
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<KakaoTokenResponseDto> kakaoCallback(
            @RequestParam("code") String code
    ) {
        KakaoTokenResponseDto token = kakaoAuthService.exchangeCodeForToken(code);
        return ResponseEntity.ok(token);
    }

    // 👉 토큰 유효성 확인 API도 추가 가능
    @GetMapping("/login/kakao/token-info")
    public ResponseEntity<String> tokenInfo(@RequestParam("access_token") String accessToken) {
        String info = kakaoAuthService.getAccessTokenInfo(accessToken);
        return ResponseEntity.ok(info);
    }
}
