package com.tavemakers.surf.domain.comment.controller;

import com.tavemakers.surf.domain.comment.dto.req.CommentCreateReqDTO;
import com.tavemakers.surf.domain.comment.dto.res.CommentListResDTO;
import com.tavemakers.surf.domain.comment.dto.res.CommentResDTO;
import com.tavemakers.surf.domain.comment.service.CommentService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.comment.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "댓글", description = "댓글 및 대댓글 관련 CRUD API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성 (루트/대댓글)", description = "rootId가 null이면 루트 댓글")
    @PostMapping("/v1/user/posts/{postId}/comments")
    public ApiResponse<CommentResDTO> createComment(@PathVariable Long postId,
                                                    @Valid @RequestBody CommentCreateReqDTO req) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        CommentResDTO response = commentService.createComment(postId, memberId, req);
        return ApiResponse.response(HttpStatus.CREATED, COMMENT_CREATED.getMessage(), response);
    }

    @Operation(summary = "댓글 목록 조회 (페이징)", description = "루트 댓글과 대댓글 모두 포함. 페이징 처리")
    @GetMapping("/v1/user/posts/{postId}/comments")
    public ApiResponse<CommentListResDTO> getComments(
            @PathVariable Long postId,
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Long memberId = SecurityUtils.getCurrentMemberId();
        CommentListResDTO data = commentService.getComments(postId, pageable, memberId);
        return ApiResponse.response(HttpStatus.OK, COMMENT_READ.getMessage(), data);
    }

    @Operation(summary = "댓글 삭제 (내 댓글만)", description = "본인이 작성한 댓글만 삭제 가능, 대댓글 존재 시 내용만 삭제(삭제된 댓글입니다 처리)")
    @DeleteMapping("/v1/user/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long postId,
                                           @PathVariable Long commentId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        commentService.deleteComment(postId, commentId, memberId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, COMMENT_DELETED.getMessage());
    }
}