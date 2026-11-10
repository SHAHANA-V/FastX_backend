package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record BusOperatorUpdateDto(

        String name,
        @Size(min = 10,max = 10,message = "Phone Number should be in 10 digits")
        String phoneNo,
        @Email(message = "Email seems to be incorrect")
        String email
) {
}
