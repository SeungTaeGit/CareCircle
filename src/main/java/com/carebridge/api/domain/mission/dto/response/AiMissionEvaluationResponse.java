package com.carebridge.api.domain.mission.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiMissionEvaluationResponse {

    @JsonProperty("isPass")
    private boolean isPass;
    private String emotion;
    private String aiComment;

    @JsonProperty("isHarmful")
    private boolean isHarmful;
    private String toxicCategory;
}