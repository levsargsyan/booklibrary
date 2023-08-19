package com.example.booklibrary.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank
    @Size(max = 255)
    @Email
    private String username;

    @NotBlank
    @Size(max = 255)
    private String password;
}






