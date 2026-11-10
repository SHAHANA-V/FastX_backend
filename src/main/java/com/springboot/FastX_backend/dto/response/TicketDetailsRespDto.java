package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TicketDetailsRespDto(

        long bookingId,
        Instant bookedAt,
        BookingStatus bookingStatus,
        double totalAmount,
        String busName,
        String busNumber,
        BusType busType,
        AcType acType,
        String busOperatorName,
        LocalDate journeyDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        String boardingStop,
        String droppingStop,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        Instant paidAt,
        List<BookingSeatRespDto> passengers
) {}
