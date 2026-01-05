package org.example.assignment.exception.advice;

import org.example.assignment.exception.AppException;
import org.example.assignment.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(String code, String message) {}

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handle(AppException e) {
        var ec = e.getErrorCode();
        return ResponseEntity
                .status(ec.status())
                .body(new ErrorResponse(ec.name(), e.getMessage()));
    }

    // @Valid 실패(커스텀 확장자 형식 등)도 ErrorCode로 잡기
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        var ec = ErrorCode.INVALID_EXTENSION_NAME;

        String msgFromAnnotation = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(err -> err.getDefaultMessage())
                .orElse(null);

        String msg = (msgFromAnnotation == null || msgFromAnnotation.isBlank())
                ? ec.message()
                : msgFromAnnotation;

        return ResponseEntity
                .status(ec.status())
                .body(new ErrorResponse(ec.name(), msg));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        var ec = ErrorCode.OPTIMISTIC_LOCK_CONFLICT;
        return ResponseEntity
                .status(ec.status())
                .body(new ErrorResponse(ec.name(), ec.message()));
    }
}
