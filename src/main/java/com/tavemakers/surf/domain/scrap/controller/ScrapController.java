package com.tavemakers.surf.domain.scrap.controller;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scraps")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/{postId}")
    public ResponseEntity<Void> add(
            @PathVariable Long postId,
            // @AuthenticationPrincipal
            @RequestParam Long memberId // 임시
    ) {
        scrapService.addScrap(memberId, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long postId,
            // @AuthenticationPrincipal
            @RequestParam Long memberId // 임시
    ) {
        scrapService.removeScrap(memberId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Page<PostResDTO>> myScraps(
            // @AuthenticationPrincipal
            @RequestParam Long memberId, // 임시
            @PageableDefault(size = 12, sort = "postedAt")
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                scrapService.getMyScraps(memberId, pageable)
        );
    }
}