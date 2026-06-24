package com.carebridge.api.domain.activity.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class GuardianDashboardResponse {
    private String seniorName;
    private String sentiment;
    private int gardenLevel;
    private List<ActivityDto> activities;
}