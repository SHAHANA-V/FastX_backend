package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewDto(
        @Min(1)
        @Max(5)
        int rating,

        @Size(max = 1000)
        String comment,

        @NotNull
        Long bookingId
) {
}
