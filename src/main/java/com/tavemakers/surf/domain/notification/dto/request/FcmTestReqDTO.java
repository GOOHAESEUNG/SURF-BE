package com.tavemakers.surf.domain.notification.dto.request;

public record FcmTestReqDTO(
        Long memberId,
        String title,
        String body
) {}