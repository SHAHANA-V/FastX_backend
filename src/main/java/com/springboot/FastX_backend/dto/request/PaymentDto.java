package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentDto(
        @NotNull
        PaymentMethod paymentMethod
) {
}
