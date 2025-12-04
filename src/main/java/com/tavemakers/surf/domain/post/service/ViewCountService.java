package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private static final String VIEW_COUNT_KEY = "post:%d:view:count";
    private static final String VIEWERS_KEY = "post:%d:viewers:%d";
    private static final Duration VIEW_COUNT_TTL = Duration.ofDays(1);

    private final StringRedisTemplate redisTemplate;

    public int increaseViewCount(Post post, Long viewerId) {
        String viewCountKey = generateViewCountKey(post.getId());
        String viewersKey = generateViewersKey(post.getId(), viewerId);

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
    }

    private String generateViewCountKey(Long postId) {
        return String.format(VIEW_COUNT_KEY, postId);
    }

    private String generateViewersKey(Long postId, Long viewerId) {
        return String.format(VIEWERS_KEY, postId, viewerId);
    }

}
