package com.tavemakers.surf.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.tavemakers.surf.domain.member.entity.Member;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberSignupResDTO {
    private Long memberId;

    private String profileImageUrl;
    private String name;

    private List<TrackResDTO> tracks;

    private String university;
    private String graduateSchool;

    private String email;
    private String phoneNumber;

    // 정적 팩토리 메서드
    public static MemberSignupResDTO from(Member member) {
        List<TrackResDTO> tracks = member.getTracks().stream()
                .map(TrackResDTO::from)
                .toList();

        return new MemberSignupResDTO(
                member.getId(),
                member.getProfileImageUrl(),
                member.getName(),
                tracks,
                member.getUniversity(),
                member.getGraduateSchool(),
                member.getEmail(),
                member.getPhoneNumber()
        );
    }
}
