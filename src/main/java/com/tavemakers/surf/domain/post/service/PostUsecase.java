package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.res.PostLikeListResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostUsecase {
    private final PostGetService postGetService;
    private final PostLikeGetService postLikeGetService;

    @Transactional(readOnly = true)
    public PostLikeListResDTO getPostLikes (Long postId) {
        postGetService.validatePost(postId);
        return postLikeGetService.getPostLikes(postId);
    }
}
