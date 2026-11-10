package com.springboot.FastX_backend.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record ReviewRespDto(
        int rating,
        String comment,
        Instant createdAtt,
        String passengerName,
        String busName,
        String busNumber,
        String source,
        String destination,
        LocalDate journeyDate
) {
}
