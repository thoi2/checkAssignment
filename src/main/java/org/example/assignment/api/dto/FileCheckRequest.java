package org.example.assignment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FileCheckRequest(
        @NotBlank
        @Size(max = 255)
        String filename
) {}
