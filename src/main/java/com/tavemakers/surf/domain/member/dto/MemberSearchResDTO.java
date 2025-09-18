package com.tavemakers.surf.domain.member.dto;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSearchResDTO {

    private String name;
    private Integer generation;
    private String track;

    public static MemberSearchResDTO of(Member member, Integer generation, String track) {
        return MemberSearchResDTO.builder()
                .name(member.getName())
                .generation(generation)
                .track(track)
                .build();
    }
}
