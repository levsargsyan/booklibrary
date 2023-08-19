package com.example.booklibrary.security.service.impl;

import com.example.booklibrary.security.dto.UserCsvDto;
import com.example.booklibrary.security.dto.UserRequestDto;
import com.example.booklibrary.security.mapper.UserMapper;
import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.service.UserFetchService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "user.data.loader.enabled", havingValue = "true")
public class UserFetchServiceImpl implements UserFetchService {

    private final UserMapper userMapper;


    @Override
    public List<UserRequestDto> fetchUsers() throws IOException {

        List<UserCsvDto> userCsvDtoList;
        Resource resource = new ClassPathResource("user-data.csv");

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<UserCsvDto> csvToBean = new CsvToBeanBuilder<UserCsvDto>(reader)
                    .withType(UserCsvDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            userCsvDtoList = csvToBean.parse();
        } catch (IOException ex) {
            log.error("Error when trying to get user data from csv file");
            throw ex;
        }

        return userCsvDtoList.stream()
                .map(userMapper::userCsvDtoToUser)
                .collect(Collectors.toList());
    }
}
