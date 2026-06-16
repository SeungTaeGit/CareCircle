package com.carebridge.api.domain.activity.dto.response;

import com.carebridge.api.domain.activity.entity.ActivityRecord;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ActivityRecordResponse {
    private String activityType;
    private int score;
    private int playTimeSeconds;
    private LocalDateTime createdAt;

    public ActivityRecordResponse(ActivityRecord record) {
        this.activityType = record.getActivityType();
        this.score = record.getScore();
        this.playTimeSeconds = record.getPlayTimeSeconds();
        this.createdAt = record.getCreatedAt();
    }
}