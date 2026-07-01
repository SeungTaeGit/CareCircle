package com.carebridge.api.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class AiResultDto {
    private String stt;

    @JsonProperty("isHarmful")
    private boolean isHarmful;
    private Map<String, Integer> emotionWeights;
    private String translatedText;
}