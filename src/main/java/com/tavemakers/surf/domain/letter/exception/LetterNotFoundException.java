package com.tavemakers.surf.domain.letter.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

public class LetterNotFoundException extends BaseException {

    public LetterNotFoundException() {
        super(
                LetterErrorMessage.LETTER_NOT_FOUND.getStatus(),
                LetterErrorMessage.LETTER_NOT_FOUND.getMessage()
        );
    }
}
