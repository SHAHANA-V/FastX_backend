package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.CancellationStatus;

import java.time.Instant;
import java.time.LocalDate;

public record CancellationRespDto(
        long cancellationId,
        long bookingId,
        String passengerName,
        String passengerPhone,
        String busName,
        String source,
        String destination,
        LocalDate journeyDate,
        String reason,
        double refundAmount,
        CancellationStatus cancellationStatus,
        Instant requestedAt
) {
}
