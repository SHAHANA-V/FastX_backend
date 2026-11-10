package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AmenityDto(
        @NotBlank(message = "Amenity Name is required")
        @NotNull(message = "Amenity Name is required")
        String name,
        @NotBlank(message = "Amenity description is required")
        @NotNull(message = "Amenity description is required")
        String description
) {
}
