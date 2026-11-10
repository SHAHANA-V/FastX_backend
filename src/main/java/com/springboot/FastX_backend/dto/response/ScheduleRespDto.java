package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusType;
import com.springboot.FastX_backend.enums.ScheduleStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRespDto(
        Long scheduleId,
        Long routeId,
        LocalDate journeyDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Double fare,
        ScheduleStatus scheduleStatus,
        Long busId,
        String busNumber,
        String busName,
        String busOperatorName,
        String source,
        String destination,
        BusType busType,
        AcType acType
) {
}
