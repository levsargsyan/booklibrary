package com.example.booklibrary.security.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCsvDto {

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "phone")
    private String phone;

    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "postalZip")
    private String postalZip;

    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "password")
    private String password;

    @CsvBindByName(column = "pan")
    private String pan;

    @CsvBindByName(column = "expdate")
    @CsvDate("MMM d, yyyy")
    private LocalDate expDate;
}

