package com.springboot.FastX_backend.dto.response;

public record SeatSummaryRespDto(
        String busName,
        String busNumber,
        int totalSeats,
        long createdSeats,
        long windowSeats,
        long aisleSeats
) {
}
