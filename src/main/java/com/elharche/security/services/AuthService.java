package com.elharche.security.services;

import com.elharche.security.models.AuthRequest;
import com.elharche.security.models.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthResponse.AuthResponseBuilder login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(authRequest.getEmail()));
            String refreshToken = jwtService.generateRefreshToken(userDetailsService.loadUserByUsername(authRequest.getEmail()));
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken);
        }

        return AuthResponse.builder();
    }
}
