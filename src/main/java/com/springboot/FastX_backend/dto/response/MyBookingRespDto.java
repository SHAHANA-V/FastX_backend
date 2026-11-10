package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.ScheduleStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MyBookingRespDto(
        long bookingId,
        Instant bookedAt,
        BookingStatus bookingStatus,
        double totalAmount,
        ScheduleStatus scheduleStatus,
        String busName,
        String busNumber,
        LocalDate journeyDate,
        LocalTime departureTime,
        String boardingStop,
        String droppingStop,
        List<BookingSeatRespDto> passengers
) {
}
