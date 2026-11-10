package com.springboot.FastX_backend.dto.request;

public record ChangePasswordDto(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
