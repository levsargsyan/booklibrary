package com.example.booklibrary.security.service.impl;

import com.example.booklibrary.security.repository.UserRepository;
import com.example.booklibrary.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
}
