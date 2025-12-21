package com.tavemakers.surf.domain.home.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HomeContentUpsertReqDTO(

        @Schema(description = "홈 문구", example = "TAVE 신규 회원을 환영합니다.")
        @NotBlank @Size(max = 2000)
        String mainText
) {}