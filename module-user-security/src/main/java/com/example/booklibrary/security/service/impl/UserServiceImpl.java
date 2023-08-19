package com.example.booklibrary.security.service.impl;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.mapper.UserMapper;
import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.repository.UserRepository;
import com.example.booklibrary.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.example.booklibrary.security.constant.Role.ADMIN;
import static com.example.booklibrary.security.constant.Role.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final StringEncryptor encryptor;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.usersToUserResponseDtos(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUser(Long id) {
        return userRepository.findById(id)
                .map(userMapper::userToUserResponseDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .map(userMapper::userToUserResponseDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    @Override
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        Optional<User> existingUserOpt = Optional.ofNullable(userRequestDto.getId())
                .flatMap(userRepository::findById);

        existingUserOpt.ifPresentOrElse(
                existingUser -> adjustForExistingUser(userRequestDto, existingUser),
                () -> adjustForNewUser(userRequestDto)
        );

        User userToSave = userMapper.userRequestDtoToUser(userRequestDto);
        userToSave = userRepository.save(userToSave);
        return userMapper.userToUserResponseDto(userToSave);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto updatedUserRequestDto) {
        UserResponseDto existingUserDto = getUser(id);
        checkData(updatedUserRequestDto, existingUserDto);
        updatedUserRequestDto.setId(existingUserDto.getId());
        updatedUserRequestDto.setVersion(existingUserDto.getVersion());
        return saveUser(updatedUserRequestDto);
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

    @Transactional(readOnly = true)
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

    private void adjustForNewUser(UserRequestDto userDto) {
        userDto.setPan(encryptor.encrypt(userDto.getPan()));
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    private void adjustForExistingUser(UserRequestDto userDto, User existingUser) {
        if (!isSamePan(userDto.getPan(), existingUser.getPan())) {
            userDto.setPan(encryptor.encrypt(userDto.getPan()));
        } else {
            userDto.setPan(existingUser.getPan());
        }

        if (!isSamePassword(userDto.getPassword(), existingUser.getPassword())) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        } else {
            userDto.setPassword(existingUser.getPassword());
        }
    }

    private boolean isSamePan(String panFromDto, String encryptedPanFromEntity) {
        return panFromDto.equals(encryptor.decrypt(encryptedPanFromEntity));
    }

    private boolean isSamePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
