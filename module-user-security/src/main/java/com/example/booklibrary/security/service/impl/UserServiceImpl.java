package com.example.booklibrary.security.service.impl;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.mapper.UserMapper;
import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.repository.UserRepository;
import com.example.booklibrary.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static com.example.booklibrary.security.constant.Role.ADMIN;
import static com.example.booklibrary.security.constant.Role.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.usersToUserResponseDtos(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.userToUserResponseDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        User user = userRepository.save(userMapper.userRequestDtoToUser(userRequestDto));
        return userMapper.userToUserResponseDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto updatedUserRequestDto) {
        UserResponseDto existingUserDto = getUser(id);
        checkData(updatedUserRequestDto, existingUserDto);
        if (Objects.nonNull(existingUserDto)) {
            updatedUserRequestDto.setId(existingUserDto.getId());
            updatedUserRequestDto.setVersion(existingUserDto.getVersion());
            return saveUser(updatedUserRequestDto);
        }

        return null;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Role getRoleOfUser(Long id) {
        return getUser(id).getRole();
    }

    @Override
    public void checkAuthorization(Role roleOfUserToActUpon, Role roleOfRequestDto) {
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_" + SUPER_ADMIN))
                && (roleOfRequestDto == ADMIN || roleOfUserToActUpon == ADMIN || roleOfUserToActUpon == SUPER_ADMIN)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to operate on users of higher role");
        }
    }

    @Transactional
    @Override
    public void checkData(UserRequestDto requestDto, UserResponseDto existingDto) {
        if (existingDto == null) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use.");
            }
        } else {
            boolean isIsbnDifferentFromExisting = !requestDto.getEmail()
                    .equals(existingDto.getEmail());

            if (isIsbnDifferentFromExisting && userRepository.existsByEmail(requestDto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use.");
            }
        }
    }
}
