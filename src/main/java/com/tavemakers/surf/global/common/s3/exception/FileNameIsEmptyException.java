package com.tavemakers.surf.global.common.s3.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

import static com.tavemakers.surf.global.common.s3.exception.ErrorMessage.FILENAME_IS_EMPTY;

public class FileNameIsEmptyException extends BaseException {
    public FileNameIsEmptyException() {
        super(FILENAME_IS_EMPTY.getStatus(), FILENAME_IS_EMPTY.getMessage());
    }
}
