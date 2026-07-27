package com.carebridge.api.domain.mission.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionProposeRequest {
    private Long guardianId;
    private String content;
}