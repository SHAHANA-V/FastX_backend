package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public record AdminBookingRespDto(
        long bookingId,
        String passengerName,
        String passengerPhone,
        String busName,
        String busNumber,
        String operatorName,
        String source,
        String destination,
        LocalDate journeyDate,
        String seatNumbers,
        double totalAmount,
        BookingStatus bookingStatus,
        PaymentStatus paymentStatus

) {
}
