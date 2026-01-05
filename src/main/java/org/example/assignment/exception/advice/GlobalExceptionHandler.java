package org.example.assignment.exception.advice;

import org.example.assignment.exception.AppException;
import org.example.assignment.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        var ec = ErrorCode.OPTIMISTIC_LOCK_CONFLICT;
        return ResponseEntity
                .status(ec.status())
                .body(new ErrorResponse(ec.name(), ec.message()));
    }
}
