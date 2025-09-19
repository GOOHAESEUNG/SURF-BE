package com.tavemakers.surf.domain.badge.dto.request;

import com.tavemakers.surf.domain.badge.entity.BadgeType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record MemberBadgeReqDTO(
        @NotNull BadgeType badgeType,
        @NotNull List<Long> memberIdList,
        @NotNull Integer generation,
        LocalDate awardedAt
) {
}
