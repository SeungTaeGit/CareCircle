package com.carebridge.api.domain.senior.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeniorCreateRequest {
    private String name;
    private String gender;
    private String birthDate;
}