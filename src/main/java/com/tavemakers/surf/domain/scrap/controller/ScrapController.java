package com.tavemakers.surf.domain.scrap.controller;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import com.tavemakers.surf.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scraps")
public class ScrapController {

    private final ScrapService scrapService;

    /** 스크랩 추가 (현재 로그인 사용자 기준) */
    @PostMapping("/{postId}")
    public ResponseEntity<Void> addScrap(@PathVariable Long postId) {
        Long me = SecurityUtils.getCurrentMemberId();
        scrapService.addScrap(me, postId);
        return ResponseEntity.noContent().build();
    }

    /** 스크랩 삭제 (현재 로그인 사용자 기준) */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeScrap(@PathVariable Long postId) {
        Long me = SecurityUtils.getCurrentMemberId();
        scrapService.removeScrap(me, postId);
        return ResponseEntity.noContent().build();
    }

    /** 내가 스크랩한 게시글 목록 */
    @GetMapping("/me")
    public ResponseEntity<Page<PostResDTO>> myScraps(
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long me = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(scrapService.getMyScraps(me, pageable));
    }
}