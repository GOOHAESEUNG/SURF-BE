package com.tavemakers.surf.domain.feedback.controller;

import com.tavemakers.surf.domain.feedback.dto.req.FeedbackCreateReqDTO;
import com.tavemakers.surf.domain.feedback.dto.res.FeedbackResDTO;
import com.tavemakers.surf.domain.feedback.service.FeedbackService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "피드백")
public class FeedbackController {

    private final FeedbackService feedbackService;

    /** 피드백 생성 (로그인 사용자) */
    @Operation(summary = "피드백 생성")
    @PostMapping("/v1/user/feedbacks")
    public ResponseEntity<FeedbackResDTO> createFeedback(
            @Valid @RequestBody FeedbackCreateReqDTO req
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(feedbackService.createFeedback(req, memberId));
    }

    /** 피드백 조회 (운영진 전용) */
    @Operation(summary = "피드백 조회")
    @GetMapping("/v1/admin/feedbacks")
    public ResponseEntity<Page<FeedbackResDTO>> getFeedbacks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacks(pageable));
    }
}
