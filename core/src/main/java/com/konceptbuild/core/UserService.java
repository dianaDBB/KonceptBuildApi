package com.konceptbuild.core;

import com.konceptbuild.core.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserEntity> findActiveByUsername(String username);

    UserEntity create(String username, String passwordHash);
}
