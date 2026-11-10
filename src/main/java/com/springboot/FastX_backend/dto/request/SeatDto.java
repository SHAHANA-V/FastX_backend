package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatDto(
        @NotBlank(message = "Seat number is required")
        @NotNull(message = "Seat number is required")
        String seatNumber,
        SeatType seatType
) {
}
