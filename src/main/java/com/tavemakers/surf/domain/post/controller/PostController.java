package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.service.PostService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.post.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "게시물", description = "이번 MVP 게시물 조회를 위해 임의로 작성한 API입니다. 추후 MVP를 통해 변경예정")
public class PostController {

    private final PostService postService;

    /** 게시글 생성 (작성자 = 현재 로그인 사용자) */
    @Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
    @PostMapping("/v1/user/posts")
    public ApiResponse<PostResDTO> createPost(
            @Valid @RequestBody PostCreateReqDTO req
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        PostResDTO response = postService.createPost(req, memberId);
        return ApiResponse.response(HttpStatus.CREATED, POST_CREATED.getMessage(), response);
    }

    /** 게시글 단건 조회 (뷰어 = 현재 로그인 사용자; 스크랩 여부 등 계산용) */
    @Operation(summary = "게시글 단건 조회", description = "특정 ID의 게시글을 조회합니다.")
    @GetMapping("/v1/user/posts/{postId}")
    public ApiResponse<PostResDTO> getPost(
            @PathVariable(name = "postId") Long postId
    ) {
        Long viewerId = SecurityUtils.getCurrentMemberId();
        PostResDTO response = postService.getPost(postId, viewerId);
        return ApiResponse.response(HttpStatus.OK, POST_READ.getMessage(), response);
    }

    /** 내가 작성한 게시글 목록 */
    @Operation(summary = "내가 작성한 게시글", description = "본인이 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/v1/user/posts/me")
    public ApiResponse<Slice<PostResDTO>> getMyPosts(
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long me = SecurityUtils.getCurrentMemberId();
        Slice<PostResDTO> response = postService.getMyPosts(me, pageable);
        return ApiResponse.response(HttpStatus.OK, MY_POSTS_READ.getMessage(), response);
    }

    /** 특정 작성자의 게시글 목록 (뷰어 = 현재 로그인 사용자) */
    @Operation(summary = "특정 작성자의 게시글 목록", description = "특정 작성자가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/v1/user/posts/author/{authorId}")
    public ApiResponse<Slice<PostResDTO>> getPostsByMember(
            @PathVariable (name = "authorId") Long authorId,
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long viewerId = SecurityUtils.getCurrentMemberId();
        Slice<PostResDTO> response = postService.getPostsByMember(authorId, viewerId, pageable);
        return ApiResponse.response(HttpStatus.OK, POSTS_BY_MEMBER_READ.getMessage(), response);
    }

    /** 게시판별 게시글 목록 (뷰어 = 현재 로그인 사용자) */
    @Operation(summary = "게시판별 게시글 목록", description = "특정 게시판에 속한 게시글 목록을 조회합니다.")
    @GetMapping("/v1/user/posts/board/{boardId}")
    public ApiResponse<Slice<PostResDTO>> getPostsByBoard(
            @PathVariable(name = "boardId") Long boardId,
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long viewerId = SecurityUtils.getCurrentMemberId();
        Slice<PostResDTO> response = postService.getPostsByBoard(boardId, viewerId, pageable);
        return ApiResponse.response(HttpStatus.OK, POSTS_BY_BOARD_READ.getMessage(), response);
    }

    /** 게시글 수정 (작성자 검증은 서비스에서) */
    @Operation(summary = "게시글 수정", description = "본인이 작성한 게시글을 수정합니다.")
    @PutMapping("/v1/user/posts/{postId}")
    public ApiResponse<PostResDTO> updatePost(
            @PathVariable(name = "postId") Long postId,
            @Valid @RequestBody PostUpdateReqDTO req
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        PostResDTO response = postService.updatePost(postId, req, memberId);
        return ApiResponse.response(HttpStatus.OK, POST_UPDATED.getMessage(), response);
    }

    /** 게시글 삭제 (작성자/권한 검증은 서비스에서) */
    @Operation(summary = "게시글 삭제", description = "본인이 작성한 게시글을 삭제합니다.")
    @DeleteMapping("/v1/user/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, POST_DELETED.getMessage());
    }
}