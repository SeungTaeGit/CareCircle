package com.carebridge.api.domain.report.scheduler;

import com.carebridge.api.domain.report.service.WeeklyReportService;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final SeniorRepository seniorRepository;
    private final WeeklyReportService weeklyReportService;

    @Scheduled(cron = "0 0 2 * * MON")
    public void generateWeeklyReports() {
        log.info("⏰ [스케줄러 시작] 주간 감정 리포트 자동 생성을 시작합니다.");

        List<Senior> seniors = seniorRepository.findAll();
        LocalDate today = LocalDate.now();

        int successCount = 0;
        for (Senior senior : seniors) {
            try {
                weeklyReportService.generateReportForSenior(senior, today);
                successCount++;
            } catch (Exception e) {
                log.error("🚨 어르신 ID {} 리포트 생성 실패: {}", senior.getId(), e.getMessage());
            }
        }

        log.info("⏰ [스케줄러 완료] 총 {}명의 주간 리포트 생성이 완료되었습니다.", successCount);
    }
}