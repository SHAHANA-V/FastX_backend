package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.SeatType;

public record SeatRespV2Dto(
        Long id,
        String seatNumber,
        SeatType seatType,
        boolean booked
) {
}
