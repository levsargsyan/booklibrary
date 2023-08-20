package com.example.booklibrary.security.controller;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUser(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        userService.checkAuthorization(null, userRequestDto.getRole());
        userService.checkData(userRequestDto, null);
        UserResponseDto savedUserRequestDto = userService.saveUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserRequestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto updatedUserRequestDto) {
        Role roleOfUserToActUpon = userService.getRoleOfUser(id);
        userService.checkAuthorization(roleOfUserToActUpon, updatedUserRequestDto.getRole());
        UserResponseDto userResponseDto = userService.updateUser(id, updatedUserRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Role roleOfUserToActUpon = userService.getRoleOfUser(id);
        userService.checkAuthorization(roleOfUserToActUpon, null);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
