package com.springboot.FastX_backend.dto.response;

public record AdminBusOperatorRespDto(
        long id,
        String name,
        String phoneNo,
        String email,
        String username,
        boolean active
) {
}
