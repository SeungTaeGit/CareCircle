package com.carebridge.api.domain.activity.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityDto {
    private Long id;
    private String date;
    private String missionTitle;
    private String type;
    private String contentSummary;
}