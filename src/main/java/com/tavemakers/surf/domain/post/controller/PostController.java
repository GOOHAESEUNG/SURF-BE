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
    public ResponseEntity<PostResDTO> create(
            @Valid @RequestBody PostCreateReqDTO req) {
        return ResponseEntity.ok(postService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResDTO> get(
            @PathVariable Long id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PostResDTO>> list(
            @RequestParam Long boardId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 12, sort = "postedAt") Pageable pageable // 페이징 우선 12개로 설정, 추후 변경 필요, sort는 postedAt 기준 내림차순
    ) {
        return ResponseEntity.ok(postService.listByBoard(boardId, keyword, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateReqDTO req) {
        return ResponseEntity.ok(postService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
