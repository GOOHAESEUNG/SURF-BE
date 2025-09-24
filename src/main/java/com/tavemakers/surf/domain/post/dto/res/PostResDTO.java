package com.tavemakers.surf.domain.post.dto.res;

import com.tavemakers.surf.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResDTO(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime postedAt,
        Long boardId,
        boolean scrappedByMe,
        long scrapCount
) {
    public static PostResDTO from(Post post, boolean scrappedByMe) {
        return new PostResDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isPinned(),
                post.getPostedAt(),
                post.getBoard().getId(),
                scrappedByMe,
                post.getScrapCount()
        );
    }
}
