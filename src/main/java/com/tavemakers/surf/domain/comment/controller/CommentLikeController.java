package com.tavemakers.surf.domain.comment.controller;

import com.tavemakers.surf.domain.comment.service.CommentLikeService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tavemakers.surf.domain.comment.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/comments")
@Tag(name = "댓글 좋아요", description = "댓글 좋아요 / 취소 / 상태 관련 API")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @Operation(summary = "댓글 좋아요 토글", description = "이미 누른 경우 취소, 안 누른 경우 좋아요로 변경")
    @PostMapping("/{commentId}/like")
    public ApiResponse<Map<String, Object>> toggleLike(@PathVariable Long commentId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        boolean liked = commentLikeService.toggleLike(commentId, memberId);
        return ApiResponse.response(HttpStatus.OK, COMMENT_LIKE_TOGGLED.getMessage(), Map.of("liked", liked));
    }

    @Operation(summary = "댓글 좋아요 개수 조회", description = "특정 댓글의 좋아요 개수 조회")
    @GetMapping("/{commentId}/like/count")
    public ApiResponse<Long> getLikeCount(@PathVariable Long commentId) {
        long count = commentLikeService.countLikes(commentId);
        return ApiResponse.response(HttpStatus.OK, COMMENT_LIKE_COUNT_READ.getMessage(), count);
    }

    @Operation(summary = "댓글 좋아요 여부 조회", description = "특정 댓글을 좋아요 눌렀는지 확인 (true면 좋아요 상태, false면 좋아요를 누르지 않은 상태)")
    @GetMapping("/{commentId}/like/me")
    public ApiResponse<Boolean> isLiked(@PathVariable Long commentId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        boolean liked = commentLikeService.isLikedByMe(commentId, memberId);
        return ApiResponse.response(HttpStatus.OK, COMMENT_LIKE_STATUS_READ.getMessage(), liked);
    }
}
