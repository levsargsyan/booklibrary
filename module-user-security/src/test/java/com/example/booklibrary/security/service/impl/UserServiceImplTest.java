package com.example.booklibrary.security.service.impl;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.mapper.UserMapper;
import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringEncryptor encryptor;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        User user2 = new User();
        List<User> userList = Arrays.asList(user1, user2);

        UserResponseDto dto1 = new UserResponseDto();
        UserResponseDto dto2 = new UserResponseDto();
        List<UserResponseDto> expectedDtos = List.of(dto1, dto2);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.usersToUserResponseDtos(userList)).thenReturn(expectedDtos);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(expectedDtos, result);
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).usersToUserResponseDtos(userList);
    }

    @Test
    void testGetAllUsersCount() {
        Long expectedCount = 5L;
        when(userRepository.count()).thenReturn(expectedCount);

        Long result = userService.getAllUsersCount();

        assertEquals(expectedCount, result);
        verify(userRepository, times(1)).count();
    }

    @Test
    void testGetUser_WithValidId() {
        Long userId = 1L;
        User sampleUser = new User();
        sampleUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));

        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(userId);
        when(userMapper.userToUserResponseDto(sampleUser)).thenReturn(expectedDto);

        UserResponseDto result = userService.getUser(userId);

        assertEquals(expectedDto, result);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).userToUserResponseDto(sampleUser);
    }

    @Test
    void testGetUser_WithInvalidId() {
        Long invalidUserId = 1L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            userService.getUser(invalidUserId);
        });

        verify(userRepository, times(1)).findById(invalidUserId);
        verify(userMapper, never()).userToUserResponseDto(any());
    }

    @Test
    void testGetUserByEmail_WithValidEmail() {
        String userEmail = "test@example.com";
        User sampleUser = new User();
        sampleUser.setEmail(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(sampleUser));

        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setEmail(userEmail);
        when(userMapper.userToUserResponseDto(sampleUser)).thenReturn(expectedDto);

        UserResponseDto result = userService.getUserByEmail(userEmail);

        assertEquals(expectedDto, result);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(userMapper, times(1)).userToUserResponseDto(sampleUser);
    }

    @Test
    void testGetUserByEmail_WithInvalidEmail() {
        String invalidEmail = "invalid@example.com";
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            userService.getUserByEmail(invalidEmail);
        });

        verify(userRepository, times(1)).findByEmail(invalidEmail);
        verify(userMapper, never()).userToUserResponseDto(any());
    }

    @Test
    void testSaveUser_AddNewUser() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("new@example.com");
        requestDto.setPan("123456789");
        requestDto.setPassword("password123");

        User mappedUser = new User();
        mappedUser.setEmail(requestDto.getEmail());
        when(userMapper.userRequestDtoToUser(requestDto)).thenReturn(mappedUser);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(requestDto.getEmail());
        when(userRepository.save(mappedUser)).thenReturn(savedUser);

        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(1L);
        expectedDto.setEmail(requestDto.getEmail());
        when(userMapper.userToUserResponseDto(savedUser)).thenReturn(expectedDto);

        UserResponseDto result = userService.saveUser(requestDto);

        assertEquals(expectedDto, result);
        verify(userMapper, times(1)).userRequestDtoToUser(requestDto);
        verify(userRepository, times(1)).save(mappedUser);
        verify(userMapper, times(1)).userToUserResponseDto(savedUser);
    }

    @Test
    void testSaveUser_AdjustExistingUser() {
        Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setId(userId);
        requestDto.setEmail("existing@example.com");
        requestDto.setPan("987654321");
        requestDto.setPassword("oldPassword123");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setPan("encryptedPan");
        existingUser.setPassword("encodedOldPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        User mappedUser = new User();
        mappedUser.setEmail(requestDto.getEmail());
        when(userMapper.userRequestDtoToUser(requestDto)).thenReturn(mappedUser);

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setEmail(requestDto.getEmail());
        when(userRepository.save(mappedUser)).thenReturn(savedUser);

        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(userId);
        expectedDto.setEmail(requestDto.getEmail());
        when(userMapper.userToUserResponseDto(savedUser)).thenReturn(expectedDto);

        UserResponseDto result = userService.saveUser(requestDto);

        assertEquals(expectedDto, result);
        verify(userMapper, times(1)).userRequestDtoToUser(requestDto);
        verify(userRepository, times(1)).save(mappedUser);
        verify(userMapper, times(1)).userToUserResponseDto(savedUser);
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;

        UserRequestDto updatedRequestDto = new UserRequestDto();
        updatedRequestDto.setEmail("updated@example.com");
        updatedRequestDto.setPan("newPan");
        updatedRequestDto.setPassword("newPassword123");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("existing@example.com");

        UserResponseDto existingUserDto = new UserResponseDto();
        existingUserDto.setId(userId);
        existingUserDto.setEmail("existing@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.userToUserResponseDto(existingUser)).thenReturn(existingUserDto);

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setEmail(updatedRequestDto.getEmail());

        UserResponseDto savedDto = new UserResponseDto();
        savedDto.setId(userId);
        savedDto.setEmail(updatedRequestDto.getEmail());

        when(userMapper.userRequestDtoToUser(updatedRequestDto)).thenReturn(savedUser);
        when(userRepository.save(savedUser)).thenReturn(savedUser);
        when(userMapper.userToUserResponseDto(savedUser)).thenReturn(savedDto);

        UserResponseDto result = userService.updateUser(userId, updatedRequestDto);

        assertEquals(savedDto, result);

        verify(userRepository, times(2)).findById(userId);
        verify(userMapper, times(2)).userToUserResponseDto(existingUser);
        verify(userMapper, times(1)).userRequestDtoToUser(updatedRequestDto);
        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    void testUpdateUser_NoExistingUser() {
        Long userId = 2L;
        UserRequestDto updatedRequestDto = new UserRequestDto();
        updatedRequestDto.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(userId, updatedRequestDto));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).userRequestDtoToUser(any(UserRequestDto.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testGetRoleOfUser() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setRole(Role.ADMIN);
        UserResponseDto mockUserResponseDto = new UserResponseDto();
        mockUserResponseDto.setRole(Role.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userMapper.userToUserResponseDto(mockUser)).thenReturn(mockUserResponseDto);

        Role userRole = userService.getRoleOfUser(userId);

        assertEquals(Role.ADMIN, userRole);
    }

    @Test
    void checkData_noExistingUser_newEmailAvailable() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("test@email.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertDoesNotThrow(() -> userService.checkData(requestDto, null));
    }

    @Test
    void checkData_noExistingUser_newEmailExists() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("test@email.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.checkData(requestDto, null), "Email already in use.");
    }

    @Test
    void checkData_existingUser_sameEmail() {
        UserRequestDto requestDto = new UserRequestDto();
        UserResponseDto existingDto = new UserResponseDto();

        String email = "test@email.com";
        requestDto.setEmail(email);
        existingDto.setEmail(email);

        assertDoesNotThrow(() -> userService.checkData(requestDto, existingDto));
    }

    @Test
    void checkData_existingUser_newEmailExists() {
        UserRequestDto requestDto = new UserRequestDto();
        UserResponseDto existingDto = new UserResponseDto();

        requestDto.setEmail("new@email.com");
        existingDto.setEmail("test@email.com");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.checkData(requestDto, existingDto), "Email already in use.");
    }
}






