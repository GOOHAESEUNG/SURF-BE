package com.tavemakers.surf.domain.notification.controller;

import static com.tavemakers.surf.domain.comment.controller.ResponseMessage.COMMENT_MENTION_SEARCH_SUCCESS;

import com.tavemakers.surf.domain.notification.dto.req.FcmTestReqDTO;
import com.tavemakers.surf.domain.notification.service.FcmService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/notifications")
public class FcmTestController {

    private final FcmService fcmService;

    @PostMapping("/test/push")
    public ApiResponse<Void> testPush(@RequestBody FcmTestReqDTO req) {
        fcmService.sendToMember(req.memberId(), req.title(), req.body());
        return ApiResponse.response(HttpStatus.OK, COMMENT_MENTION_SEARCH_SUCCESS.getMessage());
    }
}