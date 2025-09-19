package com.tavemakers.surf.domain.board.dto.req;

import com.tavemakers.surf.domain.board.entity.BoardType;
import jakarta.validation.constraints.NotNull;

public record BoardCreateReqDTO(
        @NotNull BoardType type
) {
}
