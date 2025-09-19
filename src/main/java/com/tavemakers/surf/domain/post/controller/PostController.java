package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @Valid @RequestBody PostCreateReqDTO req) {
        return ResponseEntity.ok(postService.createPost(req));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResDTO> getPost(
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping
    public ResponseEntity<Page<PostResDTO>> getPostsByBoard(
            @RequestParam Long boardId,
            // 페이징 우선 12개로 설정, 추후 변경 필요, sort는 postedAt 기준 내림차순
            @PageableDefault(size = 12, sort = "postedAt") Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByBoard(boardId, pageable));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateReqDTO req) {
        return ResponseEntity.ok(postService.updatePost(postId, req));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}
