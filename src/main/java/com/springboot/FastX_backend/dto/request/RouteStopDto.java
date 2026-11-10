package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.StopType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RouteStopDto(
        @NotNull(message = "stop name is required")
        @NotBlank(message = "stop name is required")
        String stopName,
        StopType stopType,
        @NotNull(message = "city name is required")
        @NotBlank(message = "city name is required")
        String cityName,

        @NotNull(message = "stop order is required")
        int stopOrder
) {
}
