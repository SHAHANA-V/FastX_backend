package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.Gender;

public record BookingSeatRespDto(
        String passengerName,
        int age,
        Gender gender,
        String seatNumber
) {
}
