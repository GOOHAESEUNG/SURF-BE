package com.tavemakers.surf.domain.notification.dto.req;

import com.tavemakers.surf.domain.notification.entity.Platform;

public record DeviceTokenReqDTO(
        String token,
        Platform platform
) {
}