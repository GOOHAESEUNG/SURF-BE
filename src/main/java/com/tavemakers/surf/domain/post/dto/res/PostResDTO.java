package com.tavemakers.surf.domain.post.dto.res;

import java.time.LocalDateTime;

public record PostResDTO(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime postedAt,
        Long boardId
) {
}
