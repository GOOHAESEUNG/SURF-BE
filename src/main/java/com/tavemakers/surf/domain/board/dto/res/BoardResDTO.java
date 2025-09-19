package com.tavemakers.surf.domain.board.dto.res;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.entity.BoardType;

public record BoardResDTO(
        Long id,
        String name,
        BoardType type
) {
    public static BoardResDTO from(Board board) {
        return new BoardResDTO(
                board.getId(),
                board.getName(),
                board.getType()
        );
    }
}