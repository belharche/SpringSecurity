package com.elharche.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class AuthRequest {
    private String email;
    private String password;
}
