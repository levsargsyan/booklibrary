package com.example.booklibrary.security.bootstrap;

import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.repository.UserRepository;
import com.example.booklibrary.security.service.UserFetchService;
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

@Component
@ConditionalOnProperty(name = "user.data.loader.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class UserDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserFetchService userFetchService;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.data.loader.sysadmin.email}")
    private String systemAdminEmail;

    @Value("${user.data.loader.sysadmin.password}")
    private String systemAdminPassword;

    @Value("${user.data.loader.admin.email}")
    private String adminEmail;

    @Value("${user.data.loader.admin.password}")
    private String adminPassword;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            if (userRepository.count() == 0) {
                List<User> users = userFetchService.fetchUsers();

                initSimpleUsers(users);
                initAdminUsers(users);

                userRepository.saveAll(users);
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

    private void initSimpleUsers(List<User> users) {
        users.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
        });
    }

    private void initAdminUsers(List<User> users) {
        String dummySysAdmData = "dummySysAdm";
        String dummyAdmData = "dummyAdm";

        User systemAdmin = new User();
        systemAdmin.setEmail(systemAdminEmail);
        systemAdmin.setPassword(passwordEncoder.encode(systemAdminPassword));
        systemAdmin.setRole(Role.SUPER_ADMIN);
        systemAdmin.setPan(dummySysAdmData);
        systemAdmin.setName(dummySysAdmData);
        systemAdmin.setPhone(dummySysAdmData);
        systemAdmin.setCountry(dummySysAdmData);
        systemAdmin.setAddress(dummySysAdmData);
        systemAdmin.setPostalZip(dummySysAdmData);
        systemAdmin.setExpDate(LocalDate.now());

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setPan(dummyAdmData);
        admin.setName(dummyAdmData);
        admin.setPhone(dummyAdmData);
        admin.setCountry(dummyAdmData);
        admin.setAddress(dummyAdmData);
        admin.setPostalZip(dummyAdmData);
        admin.setExpDate(LocalDate.now());

        users.add(systemAdmin);
        users.add(admin);
    }
}
