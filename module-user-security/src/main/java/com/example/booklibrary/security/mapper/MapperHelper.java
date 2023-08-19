package com.example.booklibrary.security.mapper;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapperHelper {

    private final StringEncryptor encryptor;

    @Named("decryptPan")
    public String decryptPan(String encryptedPan) {
        return encryptor.decrypt(encryptedPan);
    }
}
