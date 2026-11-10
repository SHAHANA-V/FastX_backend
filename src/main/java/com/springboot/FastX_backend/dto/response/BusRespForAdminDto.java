package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;

public record BusRespForAdminDto(
        long id,
        String busName,
        String busNumber,
        BusType busType,
        int totalSeats,
        AcType acType,
        BusStatus busStatus,
        String busOperatorName,
        String busOperatorPhoneNo,
        String busOperatorEmail

) {
}
