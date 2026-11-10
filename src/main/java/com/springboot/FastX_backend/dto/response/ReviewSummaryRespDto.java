package com.springboot.FastX_backend.dto.response;

public record ReviewSummaryRespDto(
        double averageRating,
        long totalReviews
) {
}
