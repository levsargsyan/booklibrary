package com.example.booklibrary.security.dto;

import com.example.booklibrary.security.constant.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 3447144497182528733L;

    private Long id;

    private Integer version;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String postalZip;

    private String country;

    private String pan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expDate;

    private Role role;
}
