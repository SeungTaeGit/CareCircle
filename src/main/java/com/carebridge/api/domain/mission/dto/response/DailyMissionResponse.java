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
        return DailyMissionResponse.builder()
                .missionId(mission.getId())
                .title(mission.getMissionTemplate().getTitle())
                .missionType(mission.getMissionTemplate().getType().name())
                .status(mission.getStatus())
                .rewardXp(mission.getMissionTemplate().getRewardXp())
                .assignedAt(mission.getAssignedAt())
                .build();
    }
}