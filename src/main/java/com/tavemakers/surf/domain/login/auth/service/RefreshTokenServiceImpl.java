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
    }

    /** ================= RTR 핵심 ================= */
    @Override
    public Long rotate(HttpServletResponse response, String refreshToken) {

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token");
        }

        Long memberId = jwtService.extractMemberId(refreshToken).orElseThrow();
        String deviceId = jwtService.extractDeviceId(refreshToken).orElseThrow();

        String key = key(memberId, deviceId);
        String stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            throw new IllegalStateException("No stored refresh token");
        }

        // refresh reuse detection
        if (!refreshToken.equals(stored)) {
            invalidateAll(memberId);
            throw new IllegalStateException("Refresh token reuse detected");
        }

        // ROTATION
        redisTemplate.delete(key);

        String newRefresh = jwtService.createRefreshToken(memberId, deviceId);
        save(newRefresh);
        jwtService.sendRefreshToken(response, newRefresh);

        return memberId;
    }

    @Override
    public void invalidate(Long memberId, String deviceId) {
        redisTemplate.delete(key(memberId, deviceId));
    }

    @Override
    public void invalidateAll(Long memberId) {
        String pattern = KEY_PREFIX + memberId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /* ================= 내부 유틸 ================= */

    private void save(String refreshToken) {
        Long memberId = jwtService.extractMemberId(refreshToken).orElseThrow();
        String deviceId = jwtService.extractDeviceId(refreshToken).orElseThrow();

        long ttlMs = jwtService.getExpiration(refreshToken) - System.currentTimeMillis();
        String redisKey = key(memberId, deviceId);
        redisTemplate.opsForValue()
                .set(key(memberId, deviceId), refreshToken, ttlMs, TimeUnit.MILLISECONDS);
        log.info("[RTR] refresh token saved. key={}, ttlMs={}", redisKey, ttlMs);
    }

    private String key(Long memberId, String deviceId) {
        return KEY_PREFIX + memberId + ":" + deviceId;
    }
}
