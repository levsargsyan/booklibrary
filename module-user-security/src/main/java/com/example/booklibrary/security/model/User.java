package com.example.booklibrary.security.model;

import com.example.booklibrary.security.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "phone", nullable = false, length = 255)
    String phone;

    @NotBlank
    @Size(max = 255)
    @Column(name = "email", unique = true, nullable = false, length = 255)
    String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "address", nullable = false, length = 255)
    String address;

    @NotBlank
    @Size(max = 255)
    @Column(name = "postal_zip", nullable = false, length = 255)
    String postalZip;

    @NotBlank
    @Size(max = 255)
    @Column(name = "country", nullable = false, length = 255)
    String country;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "password", nullable = false, length = 1000)
    String password;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "pan", nullable = false, length = 1000)
    String pan;

    @NotNull
    @Column(name = "exp_date", nullable = false)
    LocalDate expDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}
