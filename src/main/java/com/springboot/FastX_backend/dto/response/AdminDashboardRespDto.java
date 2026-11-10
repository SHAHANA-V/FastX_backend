package com.springboot.FastX_backend.dto.response;

public record AdminDashboardRespDto(
        long totalOperators,
        long totalBuses,
        long totalRoutes,
        long totalBookings
) {

}
