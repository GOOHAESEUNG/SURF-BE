package com.tavemakers.surf.domain.feedback.controller;

import com.tavemakers.surf.domain.feedback.dto.req.FeedbackCreateReqDTO;
import com.tavemakers.surf.domain.feedback.dto.res.FeedbackResDTO;
import com.tavemakers.surf.domain.feedback.service.FeedbackService;
import com.tavemakers.surf.global.util.SecurityUtils;
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
@RequestMapping("/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    /** 피드백 생성 (로그인 사용자) */
    @PostMapping
    public ResponseEntity<FeedbackResDTO> createFeedback(
            @Valid @RequestBody FeedbackCreateReqDTO req
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(feedbackService.createFeedback(req, memberId));
    }

    /** 피드백 조회 (운영진 전용) */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT','MANAGER','PRESIDENT')")
    public ResponseEntity<Page<FeedbackResDTO>> getFeedbacks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacks(pageable));
    }
}
