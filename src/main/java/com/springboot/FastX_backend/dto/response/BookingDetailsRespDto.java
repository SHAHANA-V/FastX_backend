package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingDetailsRespDto(
        long bookingId,
        Instant bookedAt,
        BookingStatus bookingStatus,
        double totalAmount,
        String busName,
        LocalDate journeyDate,
        LocalTime departureTime,
        String boardingStop,
        String droppingStop,
        List<BookingSeatRespDto> bookingSeats
) {
}
