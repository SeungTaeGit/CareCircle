package com.carebridge.api.domain.report.service;

import com.carebridge.api.domain.ai.service.CareAiService;
import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.enums.MissionStatus;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.report.dto.response.WeeklyReportResponse;
import com.carebridge.api.domain.report.entity.WeeklyReport;
import com.carebridge.api.domain.report.repository.WeeklyReportRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final ExchangeMessageRepository exchangeMessageRepository;
    private final CareAiService careAiService;

    @Transactional
    public void generateReportForSenior(Senior senior, LocalDate today) {
        LocalDate lastSunday = today.with(DayOfWeek.MONDAY).minusDays(1);
        LocalDate lastMonday = lastSunday.minusDays(6);

        LocalDateTime startDateTime = lastMonday.atStartOfDay();
        LocalDateTime endDateTime = lastSunday.atTime(LocalTime.MAX);

        List<DailyMission> missions = dailyMissionRepository.findBySeniorIdAndCompletedAtBetween(senior.getId(), startDateTime, endDateTime);
        List<ExchangeMessage> exchanges = exchangeMessageRepository.findBySenderIdAndCreatedAtBetween(senior.getId(), startDateTime, endDateTime);

        if (missions.isEmpty() && exchanges.isEmpty()) {
            saveReport(senior, lastMonday, lastSunday, "지난주에는 어르신의 미션 및 교류 활동 기록이 없습니다. 다음 주에는 더 많은 활동을 하실 수 있도록 신경 쓰겠습니다.");
            return;
        }

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("총 미션 수행 횟수: ").append(missions.size()).append("회\n");
        missions.stream()
                .filter(m -> m.getStatus() == MissionStatus.COMPLETED)
                .forEach(m -> dataBuilder.append("- 미션 [").append(m.getMissionQuestion())
                        .append("] 감정: ").append(m.getEmotion()).append("\n"));

        dataBuilder.append("\n총 교류 메시지 발송 횟수: ").append(exchanges.size()).append("회\n");
        exchanges.forEach(e -> dataBuilder.append("- 상태: ").append(e.getStatus()).append("\n"));

        String generatedContent = careAiService.generateWeeklyReport(senior.getName(), dataBuilder.toString());

        saveReport(senior, lastMonday, lastSunday, generatedContent);
        log.info("✅ {} 어르신 주간 리포트 자동 생성 완료 ({} ~ {})", senior.getName(), lastMonday, lastSunday);
    }

    private void saveReport(Senior senior, LocalDate startDate, LocalDate endDate, String content) {
        WeeklyReport report = WeeklyReport.builder()
                .senior(senior)
                .startDate(startDate)
                .endDate(endDate)
                .reportContent(content)
                .build();
        weeklyReportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<WeeklyReportResponse> getReports(com.carebridge.api.domain.report.enums.ReportStatus status) {
        List<WeeklyReport> reports;
        if (status == null) {
            reports = weeklyReportRepository.findAll();
        } else {
            reports = weeklyReportRepository.findByStatus(status);
        }

        return reports.stream().map(r -> WeeklyReportResponse.builder()
                .reportId(r.getId())
                .seniorName(r.getSenior().getName())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .reportContent(r.getReportContent())
                .status(r.getStatus().name())
                .build()).toList();
    }

    @Transactional
    public void sendReportToGuardian(Long reportId) {
        WeeklyReport report = weeklyReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리포트를 찾을 수 없습니다."));

        if (report.getStatus() == com.carebridge.api.domain.report.enums.ReportStatus.SENT) {
            throw new IllegalStateException("이미 보호자에게 전송된 리포트입니다.");
        }

        // TODO: (향후 확장) 실제 카카오톡 알림톡이나 SMS를 전송하는 로직이 들어갈 자리.
        log.info("📨 {} 어르신의 보호자에게 주간 리포트를 전송했습니다.", report.getSenior().getName());

        report.markAsSent();
    }
}