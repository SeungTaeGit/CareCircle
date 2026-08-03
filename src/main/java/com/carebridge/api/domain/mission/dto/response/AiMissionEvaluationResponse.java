package com.carebridge.api.domain.mission.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiMissionEvaluationResponse {

    private boolean isPass;
    private String emotion;
    private String aiComment;
}