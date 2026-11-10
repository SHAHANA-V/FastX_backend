package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.RouteStatus;

public record AdminRouteRespDto(
        long id,
        String source,
        String destination,
        RouteStatus routeStatus,
        long totalStops
) {
}
