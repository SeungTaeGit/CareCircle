package com.carebridge.api.domain.mission.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminMissionCreateRequest {
    private Long seniorId;
    private String content;
    private String targetDate;
}