package org.example.assignment.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    EXTENSION_NOT_FOUND(HttpStatus.NOT_FOUND, "확장자를 찾을 수 없습니다."),
    FIXED_EXTENSION_DELETE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "고정 확장자는 삭제할 수 없습니다."),
    FIXED_EXTENSION_TOGGLE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "고정 확장자만 토글할 수 있습니다."),
    DUPLICATE_EXTENSION(HttpStatus.CONFLICT, "이미 존재하는 확장자입니다."),
    CUSTOM_EXTENSION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "커스텀 확장자는 최대 200개까지 추가할 수 있습니다."),
    INVALID_EXTENSION_NAME(HttpStatus.BAD_REQUEST, "확장자 형식이 올바르지 않습니다. (1~20자, 공백/점 제외)"),
    OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "동시 수정이 발생했습니다. 새로고침 후 다시 시도해주세요."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() { return status; }
    public String message() { return message; }
}
