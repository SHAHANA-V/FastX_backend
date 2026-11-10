package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.StopType;

public record RouteStopUpdateDto(
        String stopName,
        String cityName,
        StopType stopType
) {
}
