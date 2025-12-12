package com.tavemakers.surf.domain.letter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LetterErrorMessage {

    LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [쪽지]입니다.");

    private final HttpStatus status;
    private final String message;
}
