package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record MemberInformationResDTO(
        String username,
        String profileImageUrl,
        Boolean phoneNumberPublic,
        String phoneNumber,
        String selfIntroduction,
        String link,
        String email,
        String university,
        String graduateSchool,
        String role,
        BigDecimal activityScore,
        boolean isActive,
        List<TrackResDTO> trackList,
        List<CareerResDTO> careerList
) {
    public static MemberInformationResDTO of(Member member, List<TrackResDTO> trackList, BigDecimal activityScore, List<CareerResDTO> careerList) {
        return MemberInformationResDTO.builder()
                .username(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .phoneNumberPublic(member.getPhoneNumberPublic())
                .phoneNumber(member.getPhoneNumber())
                .selfIntroduction(member.getSelfIntroduction())
                .link(member.getLink())
                .email(member.getEmail())
                .university(member.getUniversity())
                .graduateSchool(member.getGraduateSchool())
                .role(member.getRole().name())
                .activityScore(activityScore)
                .isActive((member.isActivityStatus()))
                .trackList(trackList)
                .careerList(careerList)
                .build();
    }
}
