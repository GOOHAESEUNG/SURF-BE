package com.tavemakers.surf.domain.post.dto.res;

import com.tavemakers.surf.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResDTO(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime postedAt,
        Long boardId
) {
    public static PostResDTO from(Post post) {
        return new PostResDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isPinned(),
                post.getPostedAt(),
                post.getBoard().getId()
        );
    }
}
