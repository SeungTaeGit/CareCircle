package com.carebridge.api.domain.mission.dto.response;

import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.enums.MissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DailyMissionResponse {

    private Long missionId;
    private String title;
    private String missionType;
    private MissionStatus status;
    private int rewardXp;
    private LocalDateTime assignedAt;

    public static DailyMissionResponse from(DailyMission mission) {
        boolean hasTemplate = mission.getMissionTemplate() != null;

        return DailyMissionResponse.builder()
                .missionId(mission.getId())
                .title(hasTemplate ? mission.getMissionTemplate().getTitle() : mission.getCustomContent())
                .missionType(hasTemplate ? mission.getMissionTemplate().getType().name() : "CUSTOM")
                .status(mission.getStatus())
                .rewardXp(hasTemplate ? mission.getMissionTemplate().getRewardXp() : 10)
                .assignedAt(mission.getAssignedAt())
                .build();
    }
}