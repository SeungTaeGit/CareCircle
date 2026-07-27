package com.carebridge.api.domain.mission.service;

import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.enums.MissionStatus;
import com.carebridge.api.domain.mission.exception.MissionNotFoundException;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyMissionService {

    private final DailyMissionRepository dailyMissionRepository;

    public List<DailyMission> getTodayMissions(Long seniorId) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return dailyMissionRepository.findBySeniorIdAndAssignedAtBetween(seniorId, startOfDay, endOfDay);
    }

    @Transactional
    public void completeMission(Long dailyMissionId, String audioUrl, String sttResult) {
        DailyMission mission = dailyMissionRepository.findById(dailyMissionId)
                .orElseThrow(() -> new MissionNotFoundException("해당 미션을 찾을 수 없습니다. id=" + dailyMissionId));

        if (mission.getStatus() != MissionStatus.PENDING) {
            throw new IllegalStateException("이미 완료되었거나 건너뛴 미션입니다.");
        }

        mission.completeMission(audioUrl, sttResult);

        Senior senior = mission.getSenior();
        int reward = (mission.getMissionTemplate() != null)
                ? mission.getMissionTemplate().getRewardXp()
                : 10;

        senior.addXp(reward);
    }

    @Transactional
    public void skipMission(Long dailyMissionId) {
        DailyMission mission = dailyMissionRepository.findById(dailyMissionId)
                .orElseThrow(() -> new MissionNotFoundException("해당 미션을 찾을 수 없습니다. id=" + dailyMissionId));

        if (mission.getStatus() != MissionStatus.PENDING) {
            throw new IllegalStateException("이미 완료되었거나 건너뛴 미션입니다.");
        }

        mission.skipMission();
    }
}