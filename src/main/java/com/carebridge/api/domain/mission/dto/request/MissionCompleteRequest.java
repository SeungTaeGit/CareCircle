package com.carebridge.api.domain.mission.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionCompleteRequest {
    private String audioUrl;
    private String sttResult;
}