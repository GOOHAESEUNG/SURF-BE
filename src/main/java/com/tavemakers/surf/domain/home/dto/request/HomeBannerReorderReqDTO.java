package com.tavemakers.surf.domain.home.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record HomeBannerReorderReqDTO(
        @NotNull List<@NotNull Long> orderedIds
) {
}