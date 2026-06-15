package com.carebridge.api.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeniorLoginRequest {
    private String pinCode;
}