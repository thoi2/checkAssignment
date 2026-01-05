package org.example.assignment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddCustomExtensionRequest(
        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9]+$")
        String name
) {}
