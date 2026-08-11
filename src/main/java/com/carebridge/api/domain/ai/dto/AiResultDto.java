package com.carebridge.api.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiResultDto {
    private String stt;

    @JsonProperty("isHarmful")
    private boolean isHarmful;

    private String emotion; // FEAR_ANXIETY, ANGER, SADNESS, JOY, NEUTRAL 중 1

    private String toxicCategory; // VERBAL_ABUSE, THREAT, NONE 등

    private String translatedText;
}