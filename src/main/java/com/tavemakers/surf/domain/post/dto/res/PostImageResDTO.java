package com.tavemakers.surf.domain.post.dto.res;

import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import lombok.Builder;

@Builder
public record PostImageResDTO(
        Long imageId,
        String originalUrl,
        Long postId,
        Integer sequence
) {
    public static PostImageResDTO from(PostImageUrl postImageUrl) {
        return PostImageResDTO.builder()
                .imageId(postImageUrl.getId())
                .originalUrl(postImageUrl.getOriginalUrl())
                .postId(postImageUrl.getPost().getId())
                .sequence(postImageUrl.getSequence())
                .build();
    }
}
