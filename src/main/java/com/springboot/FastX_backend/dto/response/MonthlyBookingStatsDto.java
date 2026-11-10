package com.springboot.FastX_backend.dto.response;

public record MonthlyBookingStatsDto(
        String month,
        long totalBookings
) {
}
