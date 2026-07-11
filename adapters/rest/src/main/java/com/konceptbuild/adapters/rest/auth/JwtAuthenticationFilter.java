package com.konceptbuild.adapters.rest.auth;

import com.konceptbuild.core.TokenRevocationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;

    public JwtAuthenticationFilter(JwtService jwtService, TokenRevocationService tokenRevocationService) {
        this.jwtService = jwtService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtService.TokenDetails token = jwtService.parse(authorization.substring(7));
            if (tokenRevocationService.isRevoked(token.id())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bearer token has been logged out");
                return;
            }
            String subject = token.subject();
            if (subject == null || subject.isBlank()) {
                throw new JwtException("Token has no subject");
            }

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(subject,
                    null, List.of()));
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired bearer token");
        }
    }
}
