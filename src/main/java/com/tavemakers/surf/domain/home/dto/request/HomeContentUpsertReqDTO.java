package com.tavemakers.surf.domain.home.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HomeContentUpsertReqDTO(
        @NotBlank @Size(max = 2000)
        String mainText
) {}