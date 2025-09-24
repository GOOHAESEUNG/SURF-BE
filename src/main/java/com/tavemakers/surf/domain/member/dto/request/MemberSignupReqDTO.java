package com.tavemakers.surf.domain.member.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.*;
import java.util.List;
import com.tavemakers.surf.domain.member.entity.enums.Part;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class MemberSignupReqDTO {

    private String profileImageUrl;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    // ==== 트랙 정보 (기수 + 파트) ====
    @NotEmpty(message = "트랙 정보는 최소 1개 이상 필요합니다.")
    @jakarta.validation.Valid
    private List<TrackInfo> tracks;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TrackInfo {
        @NotNull(message = "기수는 필수 입력값입니다.")
        private Integer generation;

        @NotNull(message = "파트는 필수 입력값입니다.")
        private Part part;
    }

    @NotBlank(message = "대학교는 필수 입력값입니다.")
    private String university;

    private String graduateSchool; // 선택(대학원)

    @Email(message = "올바른 이메일 형식을 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber; // 전화번호를 하이픈 써서 받을지 안 써서 받을지?
}
