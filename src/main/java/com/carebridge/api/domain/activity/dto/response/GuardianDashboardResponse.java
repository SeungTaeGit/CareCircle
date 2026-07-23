package com.carebridge.api.domain.activity.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GuardianDashboardResponse {
    private String seniorName;
    private String sentiment;
    private int gardenLevel;
    private List<ActivityDto> activities;

    private LocalDateTime lastActiveAt;
    private List<DangerSignalDto> recentDangerSignals;

    @Getter
    @Builder
    public static class DangerSignalDto {
        private Long signalId;
        private String dangerType;
        private String description;
        private LocalDateTime createdAt;
    }
}