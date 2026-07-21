package com.carebridge.api.domain.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendResponse {
    private Long seniorId;
    private String name;
    private String country;
    private String language;
    private int matchScore;
}