package com.carebridge.api.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminSignupRequest {
    private String email;
    private String password;
    private String institutionName;
    private String managerName;
}