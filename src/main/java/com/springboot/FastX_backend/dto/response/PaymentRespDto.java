package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.PaymentMethod;
import com.springboot.FastX_backend.enums.PaymentStatus;

import java.time.Instant;

public record PaymentRespDto(
        long id,
        double amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        Instant paidAt,
        long bookingId
) {
}
