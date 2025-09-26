package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.service.PostService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts")
@Tag(name = "게시물", description = "이번 MVP 게시물 조회를 위해 임의로 작성한 API입니다. 추후 MVP를 통해 변경예정")
public class PostController {

    private final PostService postService;

    /** 게시글 생성 (작성자 = 현재 로그인 사용자) */
    @Operation(summary = "게시글 생성 (작성자 = 현재 로그인 사용자)")
    @PostMapping
    public ResponseEntity<PostResDTO> createPost(
            @Valid @RequestBody PostCreateReqDTO req
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(postService.createPost(req, memberId));
    }

    /** 게시글 단건 조회 (뷰어 = 현재 로그인 사용자; 스크랩 여부 등 계산용) */
    @Operation(summary = "게시글 단건 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResDTO> getPost(
            @PathVariable Long postId
    ) {
        Long viewerId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(postService.getPost(postId, viewerId));
    }

    /** 내가 작성한 게시글 목록 */
    @Operation(summary = "내가 작성한 게시글 목록")
    @GetMapping("/me")
    public ResponseEntity<Page<PostResDTO>> getMyPosts(
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long me = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(postService.getMyPosts(me, pageable));
    }

    /** 특정 작성자의 게시글 목록 (뷰어 = 현재 로그인 사용자) */
    @Operation(summary = "특정 작성사의 게시글 목록")
    @GetMapping("/{authorId}/posts")
    public ResponseEntity<Page<PostResDTO>> getPostsByMember(
            @PathVariable Long authorId,
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long viewerId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(postService.getPostsByMember(authorId, viewerId, pageable));
    }

    /** 게시판별 게시글 목록 (뷰어 = 현재 로그인 사용자) */
    @Operation(summary = "게시판별 게시글 목록")
    @GetMapping
    public ResponseEntity<Page<PostResDTO>> getPostsByBoard(
            @RequestParam Long boardId,
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long viewerId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(postService.getPostsByBoard(boardId, viewerId, pageable));
    }

    /** 게시글 수정 (작성자 검증은 서비스에서) */
    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateReqDTO req
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(postService.updatePost(postId, req, memberId));
    }

    /** 게시글 삭제 (작성자/권한 검증은 서비스에서) */
    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}