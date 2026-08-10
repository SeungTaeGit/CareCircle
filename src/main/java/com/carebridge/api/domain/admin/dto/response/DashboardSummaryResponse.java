package com.carebridge.api.domain.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {
    private int todayTotalMissions;
    private int todayCompletedMissions;
    private double participationRate;
}