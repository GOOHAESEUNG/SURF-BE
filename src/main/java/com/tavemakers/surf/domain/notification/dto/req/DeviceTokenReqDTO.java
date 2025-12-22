package com.tavemakers.surf.domain.notification.dto.req;

import com.tavemakers.surf.domain.notification.entity.Platform;
import jakarta.validation.constraints.NotBlank;

public record DeviceTokenReqDTO(
        @NotBlank(message = "토큰은 필수입니다.")
        String token,

        @NotBlank(message = "플랫폼은 필수입니다.")
        Platform platform
) {
}