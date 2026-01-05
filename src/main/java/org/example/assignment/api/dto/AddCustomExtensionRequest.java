package org.example.assignment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddCustomExtensionRequest(
        @NotBlank(message = "확장자를 입력하세요.")
        @Size(max = 20, message = "확장자는 최대 20자리까지 가능합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "확장자는 영문/숫자만 가능합니다.")
        String name
) {}

