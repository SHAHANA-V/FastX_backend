package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.BookingStatus;

public record BookingStatusStatsDto(
        BookingStatus bookingStatus,
        long totalBookings
) {
}
