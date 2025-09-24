package com.tavemakers.surf.domain.board.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [게시판]입니다.");

    private final HttpStatus status;
    private final String message;

}