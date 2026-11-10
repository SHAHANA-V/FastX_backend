package com.springboot.FastX_backend.dto.response;

import com.springboot.FastX_backend.enums.Role;

public record DetailsDto(
        String username,
        Boolean isActive,
        Role role
) {
}
