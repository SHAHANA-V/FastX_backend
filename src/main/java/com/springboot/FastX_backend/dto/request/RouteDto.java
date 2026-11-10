package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RouteDto(
        @NotNull(message = "Source is required")
        @NotBlank(message = "Source is required")
        String source,
        @NotNull(message = "Destination is required")
        @NotBlank(message = "Destination is required")
        String destination
) {
}
