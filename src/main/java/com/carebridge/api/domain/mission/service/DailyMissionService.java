package com.carebridge.api.domain.mission.service;

import com.carebridge.api.domain.ai.service.CareAiService;
import com.carebridge.api.domain.mission.dto.response.AiMissionEvaluationResponse;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.enums.MissionStatus;
import com.carebridge.api.domain.mission.exception.MissionNotFoundException;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.enums.InterestLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyMissionService {

    private final DailyMissionRepository dailyMissionRepository;
    private final CareAiService careAiService;

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
        processReward(mission);

        evaluateAndUpdateSeniorInterestLevel(mission.getSenior());
    }

    @Transactional
    public AiMissionEvaluationResponse completeTextMission(Long dailyMissionId, String textResult) {
        DailyMission mission = getPendingMission(dailyMissionId);

        String missionTitle = mission.getMissionQuestion();
        AiMissionEvaluationResponse aiResponse = careAiService.evaluateMissionText(missionTitle, textResult);

        if (aiResponse.isPass()) {
            mission.completeTextMission(
                    textResult,
                    aiResponse.getEmotion(),
                    aiResponse.getAiComment()
            );
            processReward(mission);

            evaluateAndUpdateSeniorInterestLevel(mission.getSenior());
        } else {
            log.info("미션 통과 실패 - 미션 ID: {}, AI 코멘트: {}", dailyMissionId, aiResponse.getAiComment());
        }

        return aiResponse;
    }

    @Transactional
    public void skipMission(Long dailyMissionId) {
        DailyMission mission = dailyMissionRepository.findById(dailyMissionId)
                .orElseThrow(() -> new MissionNotFoundException("해당 미션을 찾을 수 없습니다. id=" + dailyMissionId));

        if (mission.getStatus() != MissionStatus.PENDING) {
            throw new IllegalStateException("이미 완료되었거나 건너뛴 미션입니다.");
        }

        mission.skipMission();

        evaluateAndUpdateSeniorInterestLevel(mission.getSenior());
    }

    private DailyMission getPendingMission(Long dailyMissionId) {
        DailyMission mission = dailyMissionRepository.findById(dailyMissionId)
                .orElseThrow(() -> new MissionNotFoundException("해당 미션을 찾을 수 없습니다. id=" + dailyMissionId));

        if (mission.getStatus() != MissionStatus.PENDING) {
            throw new IllegalStateException("이미 완료되었거나 건너뛴 미션입니다.");
        }
        return mission;
    }

    private void processReward(DailyMission mission) {
        Senior senior = mission.getSenior();
        int reward = (mission.getMissionTemplate() != null)
                ? mission.getMissionTemplate().getRewardXp()
                : 10;
        senior.addXp(reward);
    }

    // =========================================================================
    // [핵심 로직] 기획안 조건표 기반 관심 수준(InterestLevel) 평가 및 갱신 엔진
    // =========================================================================
    private void evaluateAndUpdateSeniorInterestLevel(Senior senior) {
        List<DailyMission> recentMissions = dailyMissionRepository.findTop3BySeniorIdOrderByAssignedAtDesc(senior.getId());

        if (recentMissions.isEmpty()) return;

        int negativeEmotionCount = 0;
        boolean hasLonely = false;
        boolean hasUrgent = false;
        boolean hasConsecutiveSkips = false;

        if (recentMissions.size() >= 2) {
            if (recentMissions.get(0).getStatus() == MissionStatus.SKIPPED &&
                    recentMissions.get(1).getStatus() == MissionStatus.SKIPPED) {
                hasConsecutiveSkips = true;
            }
        }

        for (DailyMission mission : recentMissions) {
            String emotion = mission.getEmotion();
            if (emotion != null && mission.getStatus() == MissionStatus.COMPLETED) {
                if (emotion.equals("LONELY")) {
                    hasLonely = true;
                    negativeEmotionCount++;
                } else if (emotion.equals("SAD") || emotion.equals("ANXIOUS") || emotion.equals("CONFUSED")) {
                    negativeEmotionCount++;
                } else if (emotion.equals("URGENT") || emotion.equals("CRITICAL")) {
                    hasUrgent = true;
                }
            }
        }

        InterestLevel newLevel = InterestLevel.NONE;
        String newAction = "-";

        if (hasUrgent) {
            newLevel = InterestLevel.URGENT;
            newAction = "안전 SOP 즉시 적용";
        } else if (negativeEmotionCount >= 2) {
            newLevel = InterestLevel.CHECK;
            newAction = "안부 확인 권장";
        } else if (hasConsecutiveSkips) {
            newLevel = InterestLevel.CHECK;
            newAction = "참여 권유 권장";
        } else if (hasLonely) {
            newLevel = InterestLevel.WATCH;
            newAction = "추세 관찰";
        }

        senior.updateInterestLevel(newLevel, newAction);

        log.info("어르신 ID: {} 의 상태 갱신 완료 - 수준: {}, 조치: {}", senior.getId(), newLevel, newAction);
    }
}