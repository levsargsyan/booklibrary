package com.example.booklibrary.security.listener;

import com.example.booklibrary.security.model.User;
import com.example.booklibrary.security.util.BeanUtil;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.jasypt.encryption.StringEncryptor;

public class UserEntityListener {

    @PrePersist
    @PreUpdate
    public void prePersist(User user) {
        StringEncryptor encryptor = BeanUtil.getBean(StringEncryptor.class);

        user.setPan(encryptor.encrypt(user.getPan()));
    }

    @PostLoad
    public void postLoad(User user) {
        StringEncryptor encryptor = BeanUtil.getBean(StringEncryptor.class);

        user.setPan(encryptor.decrypt(user.getPan()));
    }
}
