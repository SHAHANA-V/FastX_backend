package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.StopType;

public record RouteStopRespDto(
        Long id,
        String stopName,
        String cityName,
        StopType stopType,
        int stopOrder
) {
}
