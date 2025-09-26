package com.tavemakers.surf.domain.member.dto.response;

import com.tavemakers.surf.domain.member.entity.Member;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record MyPageProfileResDTO(
        String username,
        String phoneNumber,
        String email,
        String university,
        String graduateSchool,
        String role,
        BigDecimal activityScore,
        List<TrackResDTO> trackList,
        List<CareerResDTO> careerList
) {
    public static MyPageProfileResDTO of(Member member, List<TrackResDTO> trackList, BigDecimal activityScore, List<CareerResDTO> careerList) {
        return MyPageProfileResDTO.builder()
                .username(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .university(member.getUniversity())
                .graduateSchool(member.getGraduateSchool())
                .role(member.getRole().name())
                .activityScore(activityScore)
                .trackList(trackList)
                .careerList(careerList)
                .build();
    }
}
