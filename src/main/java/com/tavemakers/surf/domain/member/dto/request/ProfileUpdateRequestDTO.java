package com.tavemakers.surf.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "프로필 수정 요청 DTO")
public class ProfileUpdateRequestDTO {

    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
    @Schema(description = "변경할 전화번호 (선택)", example = "010-5678-1234")
    private String phoneNumber;

    @Email(message = "올바른 이메일 형식을 입력하세요.")
    @Schema(description = "변경할 이메일 (선택)", example = "new.email@example.com")
    private String email;

    @Schema(description = "변경할 대학교 (선택)", example = "타대학교")
    private String university;

    @Schema(description = "변경할 대학원 (선택)", example = "타대학원")
    private String graduateSchool;

    @Schema(description = "새로 추가할 경력 정보 리스트 (선택)")
    private List<CareerCreateReqDTO> careersToCreate;

    @Schema(description = "수정할 기존 경력 정보 리스트 (선택)")
    private List<CareerUpdateReqDTO> careersToUpdate;

    @Schema(description = "삭제할 경력의 ID 리스트 (선택)")
    private List<Long> careerIdsToDelete;

}
