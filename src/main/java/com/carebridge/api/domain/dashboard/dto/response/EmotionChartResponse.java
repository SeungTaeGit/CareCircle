package com.carebridge.api.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmotionChartResponse {
    private String todayMood; // 오늘의 대표 감정 (예: HAPPY, 없으면 "NONE")
    private List<DailyEmotion> recentEmotions; // 최근 5일간의 날짜별 감정 데이터

    @Getter
    @Builder
    public static class DailyEmotion {
        private String date;    // 예: "08-10"
        private String emotion; // 그날의 대표 감정
    }
}