package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.res.PostLikeListResDTO;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
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
    @LogEvent("post.like.list.view")
    public PostLikeListResDTO getPostLikes (@LogParam("post_id") Long postId) {
        postGetService.validatePost(postId);
        return postLikeGetService.getPostLikes(postId);
    }
}
