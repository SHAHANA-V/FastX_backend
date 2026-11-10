package com.springboot.FastX_backend.dto.response;

public record PassengerProfileRespDto(
        long id,
        String name,
        String phoneNo,
        String email,
        String username
) {
}
