package com.tavemakers.surf.domain.scrap.controller;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "스크랩", description = "추후 MVP를 통해 디벨롭 될 예정")
public class ScrapController {

    private final ScrapService scrapService;

    /** 스크랩 추가 (현재 로그인 사용자 기준) */
    @Operation(summary = "스크랩 추가")
    @PostMapping("/v1/user/scraps/{postId}")
    public ResponseEntity<Void> addScrap(@PathVariable Long postId) {
        Long me = SecurityUtils.getCurrentMemberId();
        scrapService.addScrap(me, postId);
        return ResponseEntity.noContent().build();
    }

    /** 스크랩 삭제 (현재 로그인 사용자 기준) */
    @Operation(summary = "스크랩 삭제")
    @DeleteMapping("/v1/user/scraps/{postId}")
    public ResponseEntity<Void> removeScrap(@PathVariable Long postId) {
        Long me = SecurityUtils.getCurrentMemberId();
        scrapService.removeScrap(me, postId);
        return ResponseEntity.noContent().build();
    }

    /** 내가 스크랩한 게시글 목록 */
    @Operation(summary = "내가 스크랩한 게시글 목록")
    @GetMapping("/v1/user/scraps/me")
    public ResponseEntity<Page<PostResDTO>> myScraps(
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long me = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(scrapService.getMyScraps(me, pageable));
    }
}