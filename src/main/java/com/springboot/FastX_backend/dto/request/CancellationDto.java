package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancellationDto(
        @NotBlank
        @Size(max = 500)
        String reason
) {
}
