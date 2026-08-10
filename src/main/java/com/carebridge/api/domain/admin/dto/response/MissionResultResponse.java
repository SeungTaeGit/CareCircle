package com.carebridge.api.domain.admin.dto.response;

import com.carebridge.api.domain.mission.entity.DailyMission;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionResultResponse {
    private Long missionId;
    private String question;
    private String answer;
    private String emotion;
    private String aiComment;
    private int score;

    public static MissionResultResponse from(DailyMission mission) {
        int reward = (mission.getMissionTemplate() != null)
                ? mission.getMissionTemplate().getRewardXp() : 10;

        return MissionResultResponse.builder()
                .missionId(mission.getId())
                .question(mission.getMissionQuestion())
                .answer(mission.getSttResult())
                .emotion(mission.getEmotion())
                .aiComment(mission.getAiComment())
                .score(reward)
                .build();
    }
}