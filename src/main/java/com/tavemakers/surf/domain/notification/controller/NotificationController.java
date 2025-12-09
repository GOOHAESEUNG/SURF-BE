package com.tavemakers.surf.domain.notification.controller;

import com.tavemakers.surf.domain.notification.dto.res.NotificationResDTO;
import com.tavemakers.surf.domain.notification.entity.NotificationCategory;
import com.tavemakers.surf.domain.notification.service.NotificationService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.tavemakers.surf.domain.notification.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "알림")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResDTO>> getNotifications(
            @RequestParam(required = false) NotificationCategory category
    ) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        List<NotificationResDTO> response = notificationService.getNotifications(memberId, category);
        return ApiResponse.response(HttpStatus.OK, NOTIFICATION_READ.getMessage(), response);
    }
}
