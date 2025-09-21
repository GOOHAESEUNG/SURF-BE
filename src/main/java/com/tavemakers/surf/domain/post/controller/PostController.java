package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.service.PostService;
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
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResDTO> createPost(
            @Valid @RequestBody PostCreateReqDTO req,
            @RequestParam Long memberId) {
        return ResponseEntity.ok(postService.createPost(req, memberId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResDTO> getPost(
            @PathVariable Long postId,
            @RequestParam Long memberId) {
        return ResponseEntity.ok(postService.getPost(postId, memberId));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<PostResDTO>> getMyPosts(
            @RequestParam Long memberId, // 추후 수정
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByMember(memberId, memberId, pageable));
    }

    @GetMapping("/{authorId}/posts")
    public ResponseEntity<Page<PostResDTO>> getPostsByMember(
            @PathVariable Long authorId,
            @RequestParam(required = false) Long viewerId, // 추후 수정
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByMember(authorId, viewerId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<PostResDTO>> getPostsByBoard(
            @RequestParam Long boardId,
            @RequestParam Long memberId, // 추후 수정
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByBoard(boardId, memberId, pageable));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateReqDTO req,
            @RequestParam Long memberId // 추후 수정
    ) {
        return ResponseEntity.ok(postService.updatePost(postId, req, memberId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
