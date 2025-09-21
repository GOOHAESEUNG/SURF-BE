package com.tavemakers.surf.domain.feedback.controller;

import com.tavemakers.surf.domain.feedback.dto.req.FeedbackCreateReqDTO;
import com.tavemakers.surf.domain.feedback.dto.res.FeedbackResDTO;
import com.tavemakers.surf.domain.feedback.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResDTO> createFeedback(
            @Valid @RequestBody FeedbackCreateReqDTO req,
            @RequestParam Long memberId // 추후 수정
    ) {
        return ResponseEntity.ok(feedbackService.createFeedback(req, memberId));
    }

    // 피드백 조회 (운영진용)
    @GetMapping
    public ResponseEntity<Page<FeedbackResDTO>> getFeedbacks(
            @PageableDefault(size = 20, sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacks(pageable));
    }
}
