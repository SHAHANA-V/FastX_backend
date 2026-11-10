package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.Gender;
import com.springboot.FastX_backend.enums.IdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingSeatDto(
        @NotNull
        @NotBlank
        String passengerName,
        @NotNull
        int age,
        @NotNull
        Gender gender,
        @NotNull
        IdType idType,
        String idDocument,
        @NotNull
        Long seatId
) {
}
