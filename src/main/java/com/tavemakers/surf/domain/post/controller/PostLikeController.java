package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.service.PostLikeService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/posts/{postId}/like")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    public ApiResponse<LikeToggleRes> toggle(@PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        boolean liked = postLikeService.toggleLike(postId, memberId);
        return ApiResponse.response(HttpStatus.OK, liked ? "좋아요" : "좋아요 취소",
                new LikeToggleRes(liked));
    }

    public record LikeToggleRes(boolean liked) {}
}
