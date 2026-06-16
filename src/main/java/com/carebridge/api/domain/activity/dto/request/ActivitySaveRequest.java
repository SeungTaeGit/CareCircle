package com.carebridge.api.domain.activity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ActivitySaveRequest {
    private String activityType;
    private int score;
    private int playTimeSeconds;
}