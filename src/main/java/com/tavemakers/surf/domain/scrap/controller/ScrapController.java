package com.tavemakers.surf.domain.scrap.controller;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.scrap.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scraps")
@Tag(name = "스크랩", description = "추후 MVP를 통해 디벨롭 될 예정")
public class ScrapController {

    private final ScrapService scrapService;

    /** 스크랩 추가 (현재 로그인 사용자 기준) */
    @Operation(summary = "스크랩 추가", description = "특정 게시글을 스크랩합니다.")
    @PostMapping("/{postId}")
    public ApiResponse<Void> addScrap(@PathVariable Long postId) {
        Long me = SecurityUtils.getCurrentMemberId();
        scrapService.addScrap(me, postId);
        return ApiResponse.response(HttpStatus.CREATED, SCRAP_CREATED.getMessage());
    }

    /** 스크랩 삭제 (현재 로그인 사용자 기준) */
    @Operation(summary = "스크랩 삭제", description = "특정 게시글의 스크랩을 취소합니다.")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> removeScrap(@PathVariable Long postId) {
        Long me = SecurityUtils.getCurrentMemberId();
        scrapService.removeScrap(me, postId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, SCRAP_DELETED.getMessage());
    }

    /** 내가 스크랩한 게시글 목록 */
    @Operation(summary = "내가 스크랩한 게시글 목록", description = "본인이 스크랩한 게시글 목록을 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<Page<PostResDTO>> myScraps(
            @PageableDefault(size = 12, sort = "postedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long me = SecurityUtils.getCurrentMemberId();
        Page<PostResDTO> response = scrapService.getMyScraps(me, pageable);
        return ApiResponse.response(HttpStatus.OK, MY_SCRAP_LIST_READ.getMessage(), response);
    }
}