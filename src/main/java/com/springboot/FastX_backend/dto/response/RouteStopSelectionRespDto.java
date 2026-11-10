package com.springboot.FastX_backend.dto.response;

import java.util.List;

public record RouteStopSelectionRespDto(
        List<RouteStopRespDto> boardingStops,
        List<RouteStopRespDto> droppingStops
) {
}
