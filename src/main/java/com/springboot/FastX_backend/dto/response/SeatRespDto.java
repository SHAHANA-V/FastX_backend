package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.SeatType;

public record SeatRespDto(
        String seatNumber,
        SeatType seatType,
        String busName,
        String busNumber
) {
}
