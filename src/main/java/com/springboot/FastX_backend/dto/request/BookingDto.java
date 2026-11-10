package com.springboot.FastX_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingDto(
        @NotNull
        Long scheduleId,
        @NotNull
        Long boardingStopId,
        @NotNull
        Long droppingStopId,
        @NotEmpty
        List<@Valid BookingSeatDto> bookingSeats
) {
}
