package com.tavemakers.surf.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.tavemakers.surf.domain.member.entity.Member;

@Getter
@AllArgsConstructor
public class MemberSignupResDTO {
    private Long memberId;
    private String name;
    private String email;
    private String phoneNumber;
    private String university;
    private String graduateSchool;
    private String profileImageUrl;

    // 정적 팩토리 메서드
    public static MemberSignupResDTO from(Member member) {
        return new MemberSignupResDTO(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getUniversity(),
                member.getGraduateSchool(),
                member.getProfileImageUrl()
        );
    }
}
