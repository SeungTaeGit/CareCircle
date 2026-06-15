package com.carebridge.api.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GuardianSignupRequest {

    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    private String linkCode;
}