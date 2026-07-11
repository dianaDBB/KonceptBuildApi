package com.konceptbuild.adapters.rest.auth;

import com.konceptbuild.core.UserService;
import com.konceptbuild.core.TokenRevocationService;
import com.konceptbuild.core.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService,
                          TokenRevocationService tokenRevocationService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive a JWT")
    @ApiResponse(responseCode = "200", description = "Token issued")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        UserEntity user = userService.findActiveByUsername(request.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new TokenResponse(jwtService.createToken(user.getUsername()), "Bearer",
                jwtService.getExpirationSeconds());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Log out by revoking the current JWT")
    @SecurityRequirement(name = "bearerAuth")
    public void logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        JwtService.TokenDetails tokenDetails = jwtService.parse(token);
        tokenRevocationService.revoke(tokenDetails.id(), tokenDetails.expiresAt());
    }

    public record LoginRequest(@NotBlank String username, @NotBlank @Schema(format = "password") String password) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }
}
