package com.tavemakers.surf.domain.notification.controller;

import com.tavemakers.surf.domain.member.entity.CustomUserDetails;
import com.tavemakers.surf.domain.notification.dto.req.DeviceTokenReqDTO;
import com.tavemakers.surf.domain.notification.service.DeviceTokenRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
public class DeviceTokenPostController {

    private final DeviceTokenRegisterService deviceTokenRegisterService;

    @PostMapping("/device-tokens")
    public ResponseEntity<Void> registerDeviceToken(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody DeviceTokenReqDTO dto
    ) {
        deviceTokenRegisterService.register(user.getMember().getId(), dto);
        return ResponseEntity.ok().build();
    }
}