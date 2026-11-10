package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.Gender;
import com.springboot.FastX_backend.enums.IdType;

public record BookingPassengerRespDto(
        String passengerName,
        int age,
        Gender gender,
        IdType idType,
        String seatNumber,
        String boardingStop,
        String droppingStop
) {
}
