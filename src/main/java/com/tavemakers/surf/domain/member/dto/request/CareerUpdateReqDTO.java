package com.tavemakers.surf.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Schema(description = "기존 경력 수정 요청 DTO")
public class CareerUpdateReqDTO {

    @Schema(description = "수정할 경력의 ID (필수)", example = "1")
    private Long careerId;

    @Schema(description = "변경할 회사명 (선택)", example = "서프 컴퍼니")
    private String companyName;

    @Schema(description = "변경할 직무 (선택)", example = "시니어 개발자")
    private String position;

    @Schema(description = "변경할 근무 시작일 (선택)", example = "2024-03")
    private YearMonth startDate;

    @Schema(description = "변경할 근무 종료일 (선택, 재직 중일 경우 null 또는 미포함)", example = "2025-08")
    private YearMonth endDate;

    @Schema(description = "재직 중 여부")
    private Boolean isWorking;
}
