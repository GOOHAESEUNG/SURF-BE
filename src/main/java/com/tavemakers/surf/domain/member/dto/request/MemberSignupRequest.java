package com.tavemakers.surf.domain.member.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.*;

@Getter
public class MemberSignupRequest {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "대학교는 필수 입력값입니다.")
    private String university;

    private String graduateSchool; // 선택(대학원생)

    @Email(message = "올바른 이메일 형식을 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber; // 전화번호를 하이픈 써서 받을지 안 써서 받을지?

    private String profileImageUrl; // 프로필 이미지는 필수로 하는게 나을까요??
}
