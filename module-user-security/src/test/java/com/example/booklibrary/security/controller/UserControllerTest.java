package com.example.booklibrary.security.controller;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.jwt.JwtProvider;
import com.example.booklibrary.security.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    void testGetAllUsers() throws Exception {
        List<UserResponseDto> emptyList = Collections.emptyList();
        when(userService.getAllUsers()).thenReturn(emptyList);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() throws Exception {
        UserResponseDto userResponseDto = createUserResponseDto();
        Long userId = 1L;

        when(userService.getUser(userId)).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDto)));

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void testCreateUser() throws Exception {
        UserResponseDto userResponseDto = createUserResponseDto();
        UserRequestDto userRequestDto = createUserRequestDto();

        when(userService.saveUser(userRequestDto)).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDto)));

        verify(userService, times(1)).saveUser(userRequestDto);
    }

    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;
        UserResponseDto userResponseDto = createUserResponseDto();
        UserRequestDto updatedUserRequestDto = createUserRequestDto();

        when(userService.updateUser(userId, updatedUserRequestDto)).thenReturn(userResponseDto);

        mockMvc.perform(put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDto)));

        verify(userService, times(1)).updateUser(userId, updatedUserRequestDto);
    }

    @Test
    void testDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/v1/users/" + userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    private UserRequestDto createUserRequestDto() {
        UserRequestDto requestDto = new UserRequestDto();

        requestDto.setName("John Doe");
        requestDto.setPhone("+1234567890");
        requestDto.setEmail("johndoe_request@example.com");
        requestDto.setAddress("123 Main St.");
        requestDto.setPostalZip("12345");
        requestDto.setCountry("Exampleland");
        requestDto.setPassword("securePassword123!");
        requestDto.setPan("ABCDE1234F");
        requestDto.setExpDate(LocalDate.of(2025, 12, 31));
        requestDto.setRole(Role.USER);

        return requestDto;
    }


    private UserResponseDto createUserResponseDto() {
        UserResponseDto responseDto = new UserResponseDto();

        responseDto.setId(1L);
        responseDto.setVersion(1);
        responseDto.setName("John Doe");
        responseDto.setPhone("+1234567890");
        responseDto.setEmail("johndoe@example.com");
        responseDto.setAddress("123 Main St.");
        responseDto.setPostalZip("12345");
        responseDto.setCountry("Exampleland");
        responseDto.setPan("ABCDE1234F");
        responseDto.setExpDate(LocalDate.of(2025, 12, 31));
        responseDto.setRole(Role.USER);

        return responseDto;
    }

}
