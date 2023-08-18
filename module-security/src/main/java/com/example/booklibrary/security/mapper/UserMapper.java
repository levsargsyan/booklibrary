package com.example.booklibrary.security.mapper;

import com.example.booklibrary.security.dto.UserCsvDto;
import com.example.booklibrary.security.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User userCsvDtotoUser(UserCsvDto userCsvDto);
}
