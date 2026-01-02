package com.tavemakers.surf.global.jwt;

import com.tavemakers.surf.domain.member.entity.CustomUserDetails;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate; // 없으면 null 주입 가능

    private static final String LOGOUT_URL = "/auth/logout";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/login/")
                || uri.equals(LOGOUT_URL);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain
    ) throws ServletException, IOException {

        final String uri = request.getRequestURI();

        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);
        String accessToken = jwtService.extractAccessTokenFromHeader(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        log.debug("URI: {}, accessToken? {}, refreshToken? {}", uri, accessToken != null, refreshToken != null);

        // 1) 토큰이 아예 없는 경우 → 그냥 통과 (카카오 콜백 포함)
        if (accessToken == null && refreshToken == null) {
            chain.doFilter(request, response);
            return;
        }

        // 2) 둘 다 있는 경우: 액세스 토큰 블랙리스트만 체크하고 통과
        if (accessToken != null && refreshToken != null) {
            log.info("토큰 둘다 있는 경우");
            if (isBlacklisted(accessToken)) {
                unauthorized(response);
                return;
            }
            authenticateUser(accessToken, request);
            chain.doFilter(request, response);
            return;
        }

        // 3) 액세스만 있는 경우: 블랙리스트 체크 후 인증 주입
        if (accessToken != null) {
            log.info("Access만 있는 경우");
            if (isBlacklisted(accessToken)) {
                unauthorized(response);
                return;
            }
            authenticateUser(accessToken, request);
            chain.doFilter(request, response);
            return;
        }

        // 4) 액세스 없음 + 리프레시만 있는 경우: 재발급 후 401로 재시도 유도
        log.info("refresh만 있는 경우");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"Access token이 필요합니다. /auth/refresh로 재발급하세요.\"}");
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

    private boolean isBlacklisted(String accessToken) {
        if (redisTemplate == null) return false;
        return redisTemplate.opsForValue().get("blacklist:" + accessToken) != null;
    }

    private void unauthorized(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("This access token is blacklisted (logged out).");
    }
}