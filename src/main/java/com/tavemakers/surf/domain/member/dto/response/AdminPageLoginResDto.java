package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record AdminPageLoginResDto(
        String accessToken,
        String username,
        String role
) {
    public static AdminPageLoginResDto of(final String accessToken, Member member) {
        return AdminPageLoginResDto.builder()
                .accessToken(accessToken)
                .username(member.getName())
                .role(member.getRole().name())
                .build();
    }
}
