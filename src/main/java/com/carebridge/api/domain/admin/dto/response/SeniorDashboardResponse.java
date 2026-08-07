package com.carebridge.api.domain.admin.dto.response;

import com.carebridge.api.domain.senior.enums.InterestLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SeniorDashboardResponse {
    private Long seniorId;
    private String name;
    private String gender;
    private int age;

    private LocalDateTime lastActiveAt;
    private int thisWeekCompletedCount;
    private int thisWeekTotalCount;

    private List<String> recentEmotions;
    private InterestLevel interestLevel;
    private String recommendedAction;
}