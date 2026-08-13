package com.carebridge.api.domain.reward.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GardenResponse {
    private int plantLevel;
    private int currentExp;
    private int requiredExp;
}