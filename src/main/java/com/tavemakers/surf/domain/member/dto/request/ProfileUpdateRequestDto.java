package com.tavemakers.surf.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "프로필 수정 요청 DTO")
public class ProfileUpdateRequestDto {

    @Schema(description = "변경할 전화번호 (선택)", example = "010-5678-1234")
    private String phoneNumber;

    @Schema(description = "변경할 이메일 (선택)", example = "new.email@example.com")
    private String email;

    @Schema(description = "변경할 대학교 (선택)", example = "타대학교")
    private String university;

    @Schema(description = "변경할 대학원 (선택)", example = "타대학원")
    private String graduateSchool;

    @Schema(description = "새로 추가할 경력 정보 리스트 (선택)")
    private List<CareerCreateReqDTO> newCareers;
}
