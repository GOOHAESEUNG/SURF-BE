package com.tavemakers.surf.global.common.advice;

import com.tavemakers.surf.global.common.exception.BaseException;
import com.tavemakers.surf.global.common.exception.ErrorCode;
import com.tavemakers.surf.global.common.exception.ErrorDetail;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.tavemakers.surf.global.common.exception.ErrorCode.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        logWarning(e, e.getStatus().value());
        return responseException(e.getStatus(), e.getMessage(), null);
    }

    // Request Parameter 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorCode errorCode = PARAMETER_NOT_FOUND;
        logWarning(e, HttpStatus.BAD_REQUEST.value());
        return responseException(errorCode.getStatus(), errorCode.getMessage(), null);
    }

    // JSON 형식이 어긋난 경우 (유실, 형식X etc...)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logWarning(e, HttpStatus.BAD_REQUEST.value());
        return responseException(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    // @Valid 유효성 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ErrorDetail>>> handleMethodArgumentValidation(MethodArgumentNotValidException e) {
        ErrorCode errorCode = METHOD_ARGUMENT_NOT_VALID;

        List<ErrorDetail> errors = e.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> ErrorDetail.of(
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getRejectedValue()
                ))
                .toList();

        logWarning(e, errorCode.getStatus().value());
        return responseException(errorCode.getStatus(), errorCode.getMessage(), errors);
    }

    // No Resource Error
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        ErrorCode errorCode = RESOURCE_NOT_FOUND;
        logWarning(e, errorCode.getStatus().value());
        return responseException(errorCode.getStatus(), errorCode.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = INTERNAL_SERVER_ERROR;
        logError(e, errorCode.getStatus().value());
        return responseException(errorCode.getStatus(), e.getMessage(), null);
    }

    private <T> ResponseEntity<ApiResponse<T>> responseException(HttpStatus status, String message, T data ) {
        ApiResponse<T> response = ApiResponse.response(status, message, data);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private void logWarning(Exception e, int errorCode) {
        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), errorCode, e.getMessage());
    }

    private void logError(Exception e, int errorCode) {
        log.error(e.getMessage(), e);
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode, e.getMessage());
    }

}
