package com.carebridge.api.domain.dashboard.service;

import com.carebridge.api.domain.dashboard.dto.response.EmotionChartResponse;
import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.enums.MissionStatus;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmotionMonitoringService {

    private final DailyMissionRepository dailyMissionRepository;
    private final ExchangeMessageRepository exchangeMessageRepository;

    @Transactional(readOnly = true)
    public EmotionChartResponse getEmotionChart(Long seniorId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startDate = today.minusDays(4).atStartOfDay();
        LocalDateTime endDate = today.atTime(LocalTime.MAX);

        List<DailyMission> missions = dailyMissionRepository.findBySeniorIdAndCompletedAtBetween(seniorId, startDate, endDate);

        List<ExchangeMessage> exchanges = exchangeMessageRepository.findBySenderIdAndCreatedAtBetween(seniorId, startDate, endDate);

        List<EmotionRecord> allRecords = new ArrayList<>();

        missions.stream()
                .filter(m -> m.getStatus() == MissionStatus.COMPLETED && m.getEmotion() != null)
                .forEach(m -> allRecords.add(new EmotionRecord(m.getCompletedAt(), m.getEmotion())));

        exchanges.stream()
                .filter(e -> e.getEmotion() != null)
                .forEach(e -> allRecords.add(new EmotionRecord(e.getCreatedAt(), e.getEmotion())));

        allRecords.sort(Comparator.comparing(r -> r.time));

        Map<LocalDate, String> dailyEmotionMap = new HashMap<>();
        for (EmotionRecord record : allRecords) {
            dailyEmotionMap.put(record.time.toLocalDate(), record.emotion);
        }

        List<EmotionChartResponse.DailyEmotion> chartData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 4; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            chartData.add(EmotionChartResponse.DailyEmotion.builder()
                    .date(date.format(formatter))
                    .emotion(dailyEmotionMap.getOrDefault(date, "NONE")) // 데이터가 없으면 NONE
                    .build());
        }

        String todayMood = dailyEmotionMap.getOrDefault(today, "NONE");

        return EmotionChartResponse.builder()
                .todayMood(todayMood)
                .recentEmotions(chartData)
                .build();
    }

    private static class EmotionRecord {
        LocalDateTime time;
        String emotion;

        public EmotionRecord(LocalDateTime time, String emotion) {
            this.time = time;
            this.emotion = emotion;
        }
    }
}