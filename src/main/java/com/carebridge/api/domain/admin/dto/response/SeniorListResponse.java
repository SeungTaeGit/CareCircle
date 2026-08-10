package com.carebridge.api.domain.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeniorListResponse {
    private Long id;
    private String name;
    private int age;
    private String hobbies;
    private String matchStatus;
    private String partnerName;
    private String country;
}