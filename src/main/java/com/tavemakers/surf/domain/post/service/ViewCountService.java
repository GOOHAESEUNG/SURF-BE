package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

    private static final String VIEW_COUNT_KEY = "post:%d:view:count";
    private static final String VIEWERS_KEY = "post:%d:viewers:%d";
    private static final Duration VIEW_COUNT_TTL = Duration.ofDays(1);

    private final StringRedisTemplate redisTemplate;
    private final PostUpdateService postUpdateService;
    private final PostGetService postGetService;

    @Transactional
    public int increaseViewCount(Post post, Long viewerId) {
        String viewCountKey = generateViewCountKey(post.getId());
        String viewersKey = generateViewersKey(post.getId(), viewerId);

        try {
            Boolean alreadyViewed = redisTemplate.hasKey(viewersKey);
            if(Boolean.FALSE.equals(alreadyViewed)) {
                if (Boolean.FALSE.equals(redisTemplate.hasKey(viewCountKey))) {
                    redisTemplate.opsForValue().set(viewCountKey, String.valueOf(post.getViewCount()));
                }

                redisTemplate.opsForValue().increment(viewCountKey, 1);
                redisTemplate.opsForValue().set(viewersKey, "1", VIEW_COUNT_TTL);
            }

            String viewCount = redisTemplate.opsForValue().get(viewCountKey);
            return viewCount != null ? Integer.parseInt(viewCount) : post.getViewCount();
        } catch (Exception e) {
            log.error("Redis 커넥션 에러로 Database에서 조회합니다. Error: {}", e.getMessage());
            post.increaseViewCount();
            return post.getViewCount();
        }
    }

    private String generateViewCountKey(Long postId) {
        return String.format(VIEW_COUNT_KEY, postId);
    }

    private String generateViewersKey(Long postId, Long viewerId) {
        return String.format(VIEWERS_KEY, postId, viewerId);
    }

}
