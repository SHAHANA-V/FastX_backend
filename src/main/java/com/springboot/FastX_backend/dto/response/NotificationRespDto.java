package com.springboot.FastX_backend.dto.response;

import java.time.Instant;

public record NotificationRespDto(
        long id,
        String title,
        String message,
        Instant createdAt,
        boolean isRead
) {

}
