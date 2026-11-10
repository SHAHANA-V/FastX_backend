package com.springboot.FastX_backend.dto.response;

import java.util.List;

public record BusAmenityRespDto(
        String busNumber,
        String busName,
        List<String> amenities
) {
}
