package com.example.booklibrary.security.bootstrap;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.mapper.UserMapper;
import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.repository.UserRepository;
import com.example.booklibrary.security.service.UserFetchService;
import com.example.booklibrary.security.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.example.booklibrary.security.constant.Role.*;

@Component
@ConditionalOnProperty(name = "user.data.loader.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class UserDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserFetchService userFetchService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Value("${user.data.loader.superadmin.email}")
    private String superAdminEmail;

    @Value("${user.data.loader.superadmin.password}")
    private String superAdminPassword;

    @Value("${user.data.loader.admin.email}")
    private String adminEmail;

    @Value("${user.data.loader.admin.password}")
    private String adminPassword;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            if (userRepository.count() == 0) {
                List<UserRequestDto> users = userFetchService.fetchUsers();

                initSimpleUsers(users);
                initAdminUsers(users);

                users.forEach(userService::saveUser);

                log.info("Data loaded from csv file");
            } else {
                log.warn("Data already exists, therefore not loaded from csv file");
            }
        } catch (ConstraintViolationException cve) {
            log.warn("Data already initialized by another instance.");
        } catch (Exception exception) {
            log.error("Exception thrown when trying to load user data from csv file");
            throw exception;
        }
    }

    private void initSimpleUsers(List<UserRequestDto> users) {
        users.forEach(user -> user.setRole(USER));
    }

    private void initAdminUsers(List<UserRequestDto> users) {
        users.add(createUser("dummySupAdm", superAdminEmail, superAdminPassword, SUPER_ADMIN));
        users.add(createUser("dummyAdm", adminEmail, adminPassword, ADMIN));
    }

    private UserRequestDto createUser(String dummyData, String mail, String password, Role role) {
        UserRequestDto user = new UserRequestDto();
        user.setEmail(mail);
        user.setPassword(password);
        user.setRole(role);
        user.setPan(dummyData);
        user.setName(dummyData);
        user.setPhone(dummyData);
        user.setCountry(dummyData);
        user.setAddress(dummyData);
        user.setPostalZip(dummyData);
        user.setExpDate(LocalDate.now());

        return user;
    }
}
