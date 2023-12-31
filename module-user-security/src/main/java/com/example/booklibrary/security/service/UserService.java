package com.example.booklibrary.security.service;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    Long getAllUsersCount();

    UserResponseDto getUser(Long id);

    UserResponseDto getUserByEmail(String userEmail);

    UserResponseDto saveUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(Long id, UserRequestDto updatedUserRequestDto);

    void deleteUser(Long id);

    void checkData(
            UserRequestDto userRequestDto,
            UserResponseDto existingUserResponseDto);

    Role getRoleOfUser(Long id);

    void checkAuthorization(Role roleOfUserToActUpon, Role roleOfRequestDto);
}
