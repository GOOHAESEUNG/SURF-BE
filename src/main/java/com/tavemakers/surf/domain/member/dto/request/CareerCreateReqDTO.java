package com.tavemakers.surf.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Schema(description = "경력 생성 요청 DTO")
public record CareerCreateReqDTO(
        String company,
        String position,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isWorking
) {
    public Map<String, Object> buildProps() {
        List<String> changedFields = new ArrayList<>();

        if (company != null && !company.isBlank()) changedFields.add("company");
        if (position != null && !position.isBlank()) changedFields.add("position");
        if (startDate != null) changedFields.add("startDate");
        if (endDate != null) changedFields.add("endDate");
        if(isWorking) changedFields.add("isWorking");

        return Map.of("changed_fields", changedFields);
    }
}
