package com.example.booklibrary.security.dto;

import com.example.booklibrary.security.constant.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
public class UserResponseDto {

    private Long id;

    private Integer version;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String postalZip;

    private String country;

    @ToString.Exclude
    private String pan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expDate;

    private Role role;
}
