package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.ScheduleStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleUpdateDto(
        LocalDate journeyDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Double fare,
        ScheduleStatus scheduleStatus
) {
}
