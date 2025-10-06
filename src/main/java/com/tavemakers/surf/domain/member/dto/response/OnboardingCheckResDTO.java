package com.tavemakers.surf.domain.member.dto.response;

import lombok.Builder;

@Builder
public class OnboardingCheckResDTO {

    public Long memberId;
    public Boolean needOnboarding;
    public Boolean isApproved;

    public static OnboardingCheckResDTO of(Long memberId, Boolean needOnboarding, Boolean isApproved) {
        return OnboardingCheckResDTO.builder()
                .memberId(memberId)
                .needOnboarding(needOnboarding)
                .isApproved(isApproved).build();
    }
}
