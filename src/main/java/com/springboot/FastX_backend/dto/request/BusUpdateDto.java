package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;

public record BusUpdateDto(
        String busName,
        AcType acType,
        BusStatus busStatus
) {
}
