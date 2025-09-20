package com.tavemakers.surf.domain.member.dto;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSimpleResDto {
    private final Long memberId;
    private final String name;

    public static MemberSimpleResDto from(Member member) {
        return MemberSimpleResDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }
}
