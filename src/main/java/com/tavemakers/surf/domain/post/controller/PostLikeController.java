package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.service.PostLikeService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/posts/{postId}/like")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(summary = "좋아요 설정", description = "이미 좋아요 상태여도 204(No Content) 반환")
    @PutMapping
    public ResponseEntity<Void> like(@PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        postLikeService.like(postId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 해제", description = "이미 해제 상태여도 204(No Content) 반환")
    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        postLikeService.unlike(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
