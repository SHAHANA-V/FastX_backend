package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;

import java.time.LocalDate;

public record AdminBookingDataDto(
        long bookingId,
        String passengerName,
        String passengerPhone,
        String busName,
        String busNumber,
        String operatorName,
        String source,
        String destination,
        LocalDate journeyDate,
        double totalAmount,
        BookingStatus bookingStatus
) {
}
