package com.tavemakers.surf.domain.notification.dto.req;

public record FcmTestReqDTO(
        Long memberId,
        String title,
        String body
) {}