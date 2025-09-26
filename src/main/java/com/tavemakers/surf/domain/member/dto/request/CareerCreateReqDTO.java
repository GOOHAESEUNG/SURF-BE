package com.tavemakers.surf.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Valid
@Schema(description = "신규 경력 추가 요청 DTO")
public class CareerCreateReqDTO {

    @Schema(description = "회사명", example = "서프 컴퍼니")
    @NotBlank(message = "회사명은 필수입니다.")
    private String companyName;

    @Schema(description = "직무", example = "주니어 개발자")
    @NotBlank(message = "직무는 필수입니다.")
    private String position;

    @Schema(description = "근무 시작일", example = "2023-01")
    @NotNull(message = "근무 시작일은 필수입니다.")
    private YearMonth startDate;

    @Schema(description = "근무 종료일 (재직 중일 경우 null 또는 미포함)", example = "2024-01")
    private YearMonth endDate;

    @Schema(description = "재직 여부", example = "true or false")
    private boolean isWorking;

}
