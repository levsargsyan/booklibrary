package com.example.booklibrary.security.mapper;

import com.example.booklibrary.security.dto.UserCsvDto;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User userCsvDtotoUser(UserCsvDto userCsvDto);

    User userRequestDtoToUser(UserRequestDto userRequestDto);

    UserResponseDto userToUserResponseDto(User user);

    List<UserResponseDto> usersToUserResponseDtos(List<User> users);
}
