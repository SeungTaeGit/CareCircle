package com.carebridge.api.domain.mission.dto.response;

import com.carebridge.api.domain.mission.entity.DailyMission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminMissionResponse {
    private Long id;
    private Long seniorId;
    private String content;
    private String status;
    private LocalDateTime createdAt;

    public static AdminMissionResponse from(DailyMission mission) {
        return AdminMissionResponse.builder()
                .id(mission.getId())
                .seniorId(mission.getSenior().getId())
                .content(mission.getCustomContent() != null ? mission.getCustomContent() : mission.getCustomTitle())
                .status(mission.getStatus().name())
                .createdAt(mission.getAssignedAt())
                .build();
    }
}