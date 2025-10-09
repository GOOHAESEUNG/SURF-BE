package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingCheckResDTO {

    public Long memberId;
    public Boolean needOnboarding;
    public MemberStatus memberStatus;

    public static OnboardingCheckResDTO of(Long memberId, Boolean needOnboarding, MemberStatus memberStatus) {
        return OnboardingCheckResDTO.builder()
                .memberId(memberId)
                .needOnboarding(needOnboarding)
                .memberStatus(memberStatus).build();
    }
}
