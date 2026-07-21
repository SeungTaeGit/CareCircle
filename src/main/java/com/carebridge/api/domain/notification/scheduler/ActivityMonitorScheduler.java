package com.carebridge.api.domain.notification.scheduler;

import com.carebridge.api.domain.admin.entity.DangerSignal;
import com.carebridge.api.domain.admin.entity.enums.DangerType;
import com.carebridge.api.domain.admin.repository.DangerSignalRepository;
import com.carebridge.api.domain.notification.constant.SopRule;
import com.carebridge.api.domain.notification.service.NotificationService;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityMonitorScheduler {

    private final SeniorRepository seniorRepository;

    private final NotificationService notificationService;
    private final DangerSignalRepository dangerSignalRepository;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkSeniorActivity() {
        log.info("🚓 [스케줄러 실행] 어르신 48시간 미활동 장기 오프라인 순찰을 시작합니다...");

        LocalDateTime threshold = LocalDateTime.now().minusHours(48);

        List<Senior> inactiveSeniors = seniorRepository.findByLastActiveAtBefore(threshold);

        if (inactiveSeniors.isEmpty()) {
            log.info("✅ 48시간 이상 미활동 어르신이 없습니다.");
            return;
        }

        for (Senior senior : inactiveSeniors) {

            notificationService.createSopNotification(senior, SopRule.EXCHANGE_ISOLATION);

            DangerSignal dangerSignal = DangerSignal.builder()
                    .senior(senior)
                    .dangerType(DangerType.LONG_TERM_OFFLINE)
                    .description("48시간 이상 앱 접속 및 활동 없음 (고립 의심)")
                    .build();

            dangerSignalRepository.save(dangerSignal);

            log.info("🚨 [미활동 감지] 어르신 ID: {} 님에게 고립 알림 발송 및 관리자 위험 신호 등록 완료.", senior.getId());
        }

        log.info("✅ [스케줄러 종료] 순찰 완료. 총 {}건의 장기 오프라인 감지.", inactiveSeniors.size());
    }
}