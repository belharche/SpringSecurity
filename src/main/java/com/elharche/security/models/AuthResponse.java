package com.elharche.security.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
