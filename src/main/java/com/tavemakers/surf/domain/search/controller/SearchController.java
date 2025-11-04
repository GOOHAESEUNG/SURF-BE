package com.tavemakers.surf.domain.search.controller;

import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.search.service.PostSearchService;
import com.tavemakers.surf.domain.search.service.RecentSearchService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user/search")
@RequiredArgsConstructor
public class SearchController {
    private final PostSearchService postSearchService;
    private final RecentSearchService recentSearchService;

    @GetMapping("/posts")
    public ApiResponse<Slice<PostResDto>> searchPosts(
            @RequestParam String q, @AuthenticationPrincipal Long memberId, Pageable pageable) {
        Slice<Post> slice = postSearchService.search(memberId, q, pageable);
        return ApiResponse.ok(slice.map(PostResDto::from));
    }

    @GetMapping("/recent")
    public ApiResponse<List<String>> recent(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.ok(recentSearchService.getRecent10(memberId));
    }

    @DeleteMapping("/recent")
    public ApiResponse<Void> clear(@AuthenticationPrincipal Long memberId) {
        recentSearchService.clearAll(memberId);
        return ApiResponse.ok();
    }
}