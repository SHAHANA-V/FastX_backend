package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.StopType;

public record RouteStopRespV2Dto(
        String source,
        String destination,
        String stopName,
        String cityName,
        StopType stopType,
        int stopOrder
) {
}
