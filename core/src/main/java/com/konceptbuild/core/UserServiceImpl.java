package com.konceptbuild.core;

import com.konceptbuild.core.entity.UserEntity;
import com.konceptbuild.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findActiveByUsername(String username) {
        return userRepository.findByUsernameAndEnabledTrue(username);
    }

    @Override
    @Transactional
    public UserEntity create(String username, String passwordHash) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already in use");
        }
        return userRepository.save(new UserEntity(username, passwordHash));
    }
}
