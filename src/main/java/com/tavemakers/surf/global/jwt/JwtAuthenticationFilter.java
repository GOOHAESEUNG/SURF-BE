package com.tavemakers.surf.global.jwt;

import com.tavemakers.surf.domain.member.entity.CustomUserDetails;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate; // 없으면 null 주입 가능

    // 필요에 맞게 경로 조정
    private static final String LOGIN_URL = "/login/**";
    private static final String LOGOUT_URL = "/auth/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String uri = request.getRequestURI();

        // 로그인/로그아웃 등은 패스
        if (uri.startsWith(LOGIN_URL) || uri.equals(LOGOUT_URL)) {
            chain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid).orElse(null);

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid).orElse(null);

        log.debug("URI: {}, accessToken? {}, refreshToken? {}", uri, accessToken != null, refreshToken != null);

        // 둘 다 있는 경우: 액세스 토큰 블랙리스트만 체크하고 통과
        if (accessToken != null && refreshToken != null) {
            if (isBlacklisted(accessToken)) {
                unauthorized(response);
                return;
            }
            authenticateUser(accessToken, request);
            chain.doFilter(request, response);
            return;
        }

        // 액세스 없음 + 리프레시만 있는 경우: 재발급 후 401로 재시도 유도
        if (accessToken == null && refreshToken != null) {
            String newAccess = reIssueAccessToken(refreshToken);
            jwtService.sendAccessToken(response, newAccess);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token re-issued. Please retry with the new token.");
            return;
        }

        // 액세스만 있는 경우: 블랙리스트 체크 후 인증 주입
        if (accessToken != null) {
            if (isBlacklisted(accessToken)) {
                unauthorized(response);
                return;
            }
            authenticateUser(accessToken, request);
        }

        chain.doFilter(request, response);
    }

    /** AccessToken → memberId → Member 로드 → SecurityContext 주입 */
    private void authenticateUser(String accessToken, HttpServletRequest req) {
        jwtService.extractMemberId(accessToken).flatMap(memberRepository::findById).ifPresent(member -> {
            CustomUserDetails principal = new CustomUserDetails(member);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            auth
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        });
    }

    /** RefreshToken 검증 → 저장값(옵션)과 일치 시 새 AccessToken 생성 */
    private String reIssueAccessToken(String refreshToken) {
        Long memberId = jwtService.extractMemberId(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Cannot extract memberId from refresh token"));

        // (옵션) Redis에 저장된 리프레시와 일치하는지 확인
        if (redisTemplate != null) {
            String key = "refresh:" + memberId;
            String stored = redisTemplate.opsForValue().get(key);
            if (!StringUtils.hasText(stored) || !refreshToken.equals(stored)) {
                throw new IllegalArgumentException("Invalid refresh token (not stored or mismatched).");
            }
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for refresh token"));

        // role은 DB에서 가져와 넣어줌
        return jwtService.createAccessToken(member.getId(), member.getRole().name());
    }

    private boolean isBlacklisted(String accessToken) {
        if (redisTemplate == null) return false;
        return redisTemplate.opsForValue().get("blacklist:" + accessToken) != null;
    }

    private void unauthorized(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("This access token is blacklisted (logged out).");
    }
}