package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.SeatType;

public record SeatAvailabilityRespDto(
        String seatNumber,
        SeatType seatType,
        boolean available
) {
}
