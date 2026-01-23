package com.tavemakers.surf.domain.login.auth.service;

import com.tavemakers.surf.global.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    /** Redis key: refresh:{memberId}:{deviceId} */
    private static final String KEY_PREFIX = "refresh:";

    @Override
    public void issue(HttpServletResponse response, Long memberId, String deviceId) {
        String refreshToken = jwtService.createRefreshToken(memberId, deviceId);
        save(refreshToken);
        jwtService.sendRefreshToken(response, refreshToken);

        log.info("[RTR][ISSUE] refresh token sent to response");
    }

    /** ================= RTR 핵심 ================= */
    @Override
    public Long rotate(HttpServletResponse response, String refreshToken) {
        boolean valid = jwtService.isTokenValid(refreshToken);
        // 토큰 유효성 결과
        log.info("[RTR][ROTATE] isTokenValid={}", valid);

        if (!valid) {
            throw new IllegalStateException("Invalid refresh token");
        }

        Long memberId = jwtService.extractMemberId(refreshToken).orElseThrow();
        String deviceId = jwtService.extractDeviceId(refreshToken).orElseThrow();

        // 토큰에서 추출한 식별자
        log.info("[RTR][ROTATE] extracted memberId={}", memberId);

        String key = key(memberId, deviceId);
        log.debug("[RTR][ROTATE] redisKey generated");

        String stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            throw new IllegalStateException("No stored refresh token");
        }

        // refresh reuse detection
        if (!refreshToken.equals(stored)) {
            log.error("[RTR][ROTATE] refresh reuse detected memberId={}", memberId);
            invalidateAll(memberId);
            throw new IllegalStateException("Refresh token reuse detected");
        }

        // ROTATION
        log.info("[RTR][ROTATE] rotation allowed, deleting old token");
        redisTemplate.delete(key);

        String newRefresh = jwtService.createRefreshToken(memberId, deviceId);
        save(newRefresh);
        jwtService.sendRefreshToken(response, newRefresh);

        log.info("[RTR][ROTATE] rotation success memberId={}", memberId);
        return memberId;
    }

    @Override
    public void invalidate(Long memberId, String deviceId) {
        redisTemplate.delete(key(memberId, deviceId));
    }

    @Override
    public void invalidateAll(Long memberId) {
        log.warn("[RTR][INVALIDATE-ALL] start memberId={}", memberId);

        String pattern = KEY_PREFIX + memberId + ":*";
        log.info("[RTR][INVALIDATE-ALL] pattern={}", pattern);

        Set<String> keys = redisTemplate.keys(pattern);
        log.info("[RTR][INVALIDATE-ALL] foundKeyCount={}",
                keys == null ? 0 : keys.size());

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.warn("[RTR][INVALIDATE-ALL] deleted keyCount={}", keys.size());
        } else {
            log.info("[RTR][INVALIDATE-ALL] no keys to delete");
        }
    }

    /* ================= 내부 유틸 ================= */

    private void save(String refreshToken) {
        Long memberId = jwtService.extractMemberId(refreshToken).orElseThrow();
        String deviceId = jwtService.extractDeviceId(refreshToken).orElseThrow();

        log.info("[RTR][INVALIDATE] start memberId={} deviceId={}", memberId, deviceId);

        long ttlMs = jwtService.getExpiration(refreshToken) - System.currentTimeMillis();
        if (ttlMs <= 0) {
            throw new IllegalStateException("Refresh token already expired");
        }
        String redisKey = key(memberId, deviceId);

        redisTemplate.opsForValue()
                .set(redisKey, refreshToken, ttlMs, TimeUnit.MILLISECONDS);
        log.info("[RTR] refresh token saved. key={}, ttlMs={}", redisKey, ttlMs);
    }

    private String key(Long memberId, String deviceId) {
        return KEY_PREFIX + memberId + ":" + deviceId;
    }
}
