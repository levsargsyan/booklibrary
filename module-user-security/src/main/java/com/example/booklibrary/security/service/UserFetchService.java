package com.example.booklibrary.security.service;

import com.example.booklibrary.security.model.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface UserFetchService {

    List<User> fetchUsers() throws IOException;
}
