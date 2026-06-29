package com.carebridge.api.domain.senior.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartnerProfileResponse {
    private Long partnerId;
    private String partnerName;
    private String country;
    private String language;
}