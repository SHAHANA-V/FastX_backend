package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public record ScheduleDto(
        @NotNull(message = "Journey Date is required")
        LocalDate journeyDate,
        @NotNull(message = "Departure Time is required")
        LocalTime departureTime,
        @NotNull(message = "Arrival Time is required")
        LocalTime arrivalTime,
        @NotNull(message = "Fare is required")
        double fare
) {
}
