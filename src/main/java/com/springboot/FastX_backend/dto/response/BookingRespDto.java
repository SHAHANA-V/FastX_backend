package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRespDto(
        long bookingId,
        Instant bookedAt,
        BookingStatus bookingStatus,
        double totalAmount,
        String busName,
        LocalDate journeyDate,
        LocalTime departureTime,
        String boardingStop,
        String droppingStop
) {
}
