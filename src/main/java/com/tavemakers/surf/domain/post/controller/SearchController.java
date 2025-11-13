package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.service.PostSearchService;
import com.tavemakers.surf.domain.post.service.RecentSearchService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tavemakers.surf.domain.post.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final PostSearchService postSearchService;
    private final RecentSearchService recentSearchService;

    /** 게시글 검색 + 최근검색 저장 */
    @GetMapping("/v1/user/search/posts")
    public ApiResponse<Slice<PostResDTO>> searchPosts(
            @RequestParam @NotBlank(message = "검색어를 입력해주세요") String param,
            @PageableDefault(size = 20, sort = {"postedAt","id"}) Pageable pageable) {

        param = param.trim();
        Long memberId = SecurityUtils.getCurrentMemberId();
        Slice<PostResDTO> response = postSearchService.search(memberId, param, pageable);
        return ApiResponse.response(HttpStatus.OK, SEARCH_COMPLETED.getMessage(), response);
    }

    /** 최근 검색어 10개 */
    @GetMapping("/v1/user/search/recent")
    public ApiResponse<List<String>> recent() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        List<String> response = recentSearchService.getRecent10(memberId);
        return ApiResponse.response(HttpStatus.OK, RECENT_SEARCH_READ.getMessage(),response);
    }

    /** 최근 검색어 전체 삭제 */
    @DeleteMapping("/v1/user/search/recent")
    public ApiResponse<Void> clear() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        recentSearchService.clearAll(memberId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, RECENT_SEARCH_DELETED.getMessage());
    }

    /** 최근 검색어 단건 삭제 */
    @DeleteMapping("/v1/user/search/recent/{keyword}")
    public ApiResponse<Void> deleteOne(
            @PathVariable String keyword
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        recentSearchService.deleteOne(memberId, keyword);
        return ApiResponse.response(HttpStatus.NO_CONTENT, RECENT_SEARCH_ONE_DELETED.getMessage());
    }
}