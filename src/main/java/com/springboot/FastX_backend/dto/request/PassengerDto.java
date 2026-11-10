package com.springboot.FastX_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import javax.xml.transform.sax.SAXResult;

public record PassengerDto(
        @NotNull(message = "Name is required")
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Contact is required")
        @NotNull(message = "Contact is required")
        @Size(min = 10,max = 10,message = "Contact should be a 10 digit mobile number")
        String phoneNo,

        @NotNull(message = "Email is required")
        @NotBlank(message = "Email is required")
        @Email(message = "Email seems to be invalid")
        String email,

        @NotBlank(message = "Username is required")
        @NotNull(message = "Username is required")
        String username,

        @NotNull(message = "Password is required")
        @NotBlank(message = "Password is required")
        String password

)  {
}
