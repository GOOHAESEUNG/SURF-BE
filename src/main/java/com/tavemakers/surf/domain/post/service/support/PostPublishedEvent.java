package com.tavemakers.surf.domain.post.service.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostPublishedEvent {
    private final Long postId;
}
