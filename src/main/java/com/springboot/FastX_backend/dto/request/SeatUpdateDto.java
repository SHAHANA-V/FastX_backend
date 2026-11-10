package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.SeatType;

public record SeatUpdateDto(
        String seatNumber,
        SeatType seatType
) {
}
