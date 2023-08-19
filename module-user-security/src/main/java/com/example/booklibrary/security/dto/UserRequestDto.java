package com.example.booklibrary.security.dto;

import com.example.booklibrary.security.constant.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
public class UserRequestDto {

    @Null
    private Long id;

    @Null
    private Integer version;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String phone;

    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 255)
    private String postalZip;

    @NotBlank
    @Size(max = 255)
    private String country;

    @ToString.Exclude
    @NotBlank
    @Size(max = 255)
    private String password;

    @ToString.Exclude
    @NotBlank
    @Size(max = 255)
    private String pan;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expDate;

    @NotNull
    private Role role;
}

