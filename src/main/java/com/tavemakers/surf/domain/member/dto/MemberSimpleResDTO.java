package com.tavemakers.surf.domain.member.dto;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSimpleResDTO {
    private final Long memberId;
    private final String name;

    public static MemberSimpleResDTO from(Member member) {
        return MemberSimpleResDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }
}
