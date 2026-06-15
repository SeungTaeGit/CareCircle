package com.carebridge.api.domain.senior.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeniorCreateResponse {
    private String name;
    private String pinCode;
    private String linkCode;
}