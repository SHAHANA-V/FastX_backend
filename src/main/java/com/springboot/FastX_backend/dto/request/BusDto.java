package com.springboot.FastX_backend.dto.request;

import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusDto(
        @NotNull(message = "Bus Number is required")
        @NotBlank(message = "Bus Number is required")
        String busNumber,
        @NotNull(message = "Bus Name is required")
        @NotBlank(message = "Bus Name is required")
        String busName,
        BusType busType,
        @NotNull(message = "Total Seats is required")
        int totalSeats,
        AcType acType,
        BusStatus busStatus
) {
}
