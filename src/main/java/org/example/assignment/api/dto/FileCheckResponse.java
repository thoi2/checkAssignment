package org.example.assignment.api.dto;

public record FileCheckResponse(
        boolean allowed,
        String derivedExtension,
        String reason // OK, NO_EXTENSION, BLOCKED
) {
    public static FileCheckResponse ok(String ext) {
        return new FileCheckResponse(true, ext, "OK");
    }
    public static FileCheckResponse deny(String ext, String reason) {
        return new FileCheckResponse(false, ext, reason);
    }
}
