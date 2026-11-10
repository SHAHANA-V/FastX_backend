package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusOperatorDto(
        @NotBlank(message = "Name is required")
        @NotNull(message = "Name is required")
        String name,
        @NotBlank(message = "PhoneNo is required")
        @NotNull(message = "PhoneNo is required")
        @Size(min = 10,max = 10,message = "Phone No should be in 10 digit")
        String phoneNo,
        @NotBlank(message = "Email is required")
        @NotNull(message = "Email is required")
        @Email(message = "email seems to be incorrect")
        String email,
        @NotBlank(message = "username is required")
        @NotNull(message = "username is required")
        String username,
        @NotBlank(message = "password is required")
        @NotNull(message = "password is required")
        String password
){
}
