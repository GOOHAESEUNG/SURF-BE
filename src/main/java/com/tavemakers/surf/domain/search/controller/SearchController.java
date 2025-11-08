package com.tavemakers.surf.domain.search.controller;

import com.tavemakers.surf.domain.member.entity.CustomUserDetails;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.search.service.PostSearchService;
import com.tavemakers.surf.domain.search.service.RecentSearchService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search")
public class SearchController {

    private final PostSearchService postSearchService;
    private final RecentSearchService recentSearchService;

    /** 게시글 검색 + 최근검색 저장 */
    @GetMapping("/posts")
    public ApiResponse<Slice<PostResDTO>> searchPosts(
            @RequestParam String q,
            @AuthenticationPrincipal CustomUserDetails principal, // SecurityContext에서 memberId 추출
            @PageableDefault(size = 20, sort = {"postedAt","id"}) Pageable pageable) {

        Long memberId = principal.getMember().getId();
        Slice<Post> slice = postSearchService.search(memberId, q, pageable);
        return ApiResponse.ok(slice.map(PostResDTO::from));
    }

    /** 최근 검색어 10개 */
    @GetMapping("/recent")
    public ApiResponse<List<String>> recent(@AuthenticationPrincipal CustomUserDetails principal) {
        Long memberId = principal.getMember().getId();
        return ApiResponse.ok(recentSearchService.getRecent10(memberId));
    }

    /** 최근 검색어 전체 삭제 */
    @DeleteMapping("/recent")
    public ApiResponse<Void> clear(@AuthenticationPrincipal CustomUserDetails principal) {
        Long memberId = principal.getMember().getId();
        recentSearchService.clearAll(memberId);
        return ApiResponse.ok();
    }
}