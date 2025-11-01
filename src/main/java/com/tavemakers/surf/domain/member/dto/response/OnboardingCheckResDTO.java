package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.global.logging.LogPropsProvider;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OnboardingCheckResDTO implements LogPropsProvider{

    public Long memberId;
    public Boolean needOnboarding;
    public MemberStatus memberStatus;

    public static OnboardingCheckResDTO of(Long memberId, Boolean needOnboarding, MemberStatus memberStatus) {
        return OnboardingCheckResDTO.builder()
                .memberId(memberId)
                .needOnboarding(needOnboarding)
                .memberStatus(memberStatus).build();
    }
    @Override
    public Map<String, Object> buildProps() {
        return Map.of(
                "need_onboarding", needOnboarding != null ? needOnboarding : false
        );
    }
}
