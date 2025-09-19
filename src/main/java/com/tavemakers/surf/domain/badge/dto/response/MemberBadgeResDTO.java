package com.tavemakers.surf.domain.badge.dto.response;

import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record MemberBadgeResDTO(
        String badgeName,
        Integer generation,
        String awardedAt
) {
    public static MemberBadgeResDTO from(MemberBadge memberBadge) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        String formattedDate = memberBadge.getAwardedAt().format(formatter);

        return MemberBadgeResDTO.builder()
                .badgeName(memberBadge.getBadgeName())
                .generation(memberBadge.getGeneration())
                .awardedAt(formattedDate)
                .build();
    }
}
