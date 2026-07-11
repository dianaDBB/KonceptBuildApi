package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.UserService;
import com.konceptbuild.core.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "409", description = "Username already exists")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserEntity user = userService.create(request.username(), passwordEncoder.encode(request.password()));
            return new UserResponse(user.getId(), user.getUsername());
        } catch (IllegalArgumentException | DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    public record CreateUserRequest(@NotBlank @Size(max = 100) String username,
                                    @NotBlank @Size(min = 8, max = 72) String password) {
    }

    public record UserResponse(UUID id, String username) {
    }
}
