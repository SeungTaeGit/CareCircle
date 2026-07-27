package com.carebridge.api.domain.mission.scheduler;

import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.entity.MissionTemplate;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.mission.repository.MissionTemplateRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionScheduler {

    private final SeniorRepository seniorRepository;
    private final MissionTemplateRepository missionTemplateRepository;
    private final DailyMissionRepository dailyMissionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void assignDailyMissionsToAllSeniors() {
        log.info("⏰ 매일 자정 미션 할당 스케줄러 실행 시작");

        List<Senior> seniors = seniorRepository.findAll();

        List<MissionTemplate> templates = missionTemplateRepository.findAll();

        if (templates.isEmpty()) {
            log.warn("등록된 미션 템플릿이 없어서 미션을 할당할 수 없습니다.");
            return;
        }

        Random random = new Random();

        for (Senior senior : seniors) {
            MissionTemplate randomTemplate = templates.get(random.nextInt(templates.size()));

            DailyMission dailyMission = DailyMission.builder()
                    .senior(senior)
                    .missionTemplate(randomTemplate)
                    .build();

            dailyMissionRepository.save(dailyMission);
        }

        log.info("✅ 총 {}명의 어르신에게 오늘의 미션 할당 완료", seniors.size());
    }
}