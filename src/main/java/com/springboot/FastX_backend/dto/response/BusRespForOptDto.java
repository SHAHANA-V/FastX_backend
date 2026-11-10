package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;

public record BusRespForOptDto(
        Long id,
        String busNumber,
        String busName,
        int totalSeats,
        AcType acType,
        BusType busType,
        BusStatus busStatus
) {
}
