package com.carebridge.api.domain.report.controller;

import com.carebridge.api.domain.report.dto.response.WeeklyReportResponse;
import com.carebridge.api.domain.report.enums.ReportStatus;
import com.carebridge.api.domain.report.service.WeeklyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final WeeklyReportService weeklyReportService;

    @GetMapping
    public ResponseEntity<List<WeeklyReportResponse>> getReports(
            @RequestParam(required = false) ReportStatus status) {

        List<WeeklyReportResponse> reports = weeklyReportService.getReports(status);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/{reportId}/send")
    public ResponseEntity<String> sendReport(@PathVariable Long reportId) {
        weeklyReportService.sendReportToGuardian(reportId);
        return ResponseEntity.ok("보호자에게 리포트가 성공적으로 전송되었습니다.");
    }

    @PatchMapping("/{reportId}")
    public ResponseEntity<String> updateReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> request) {

        String newContent = request.get("content");
        weeklyReportService.updateReportContent(reportId, newContent);

        return ResponseEntity.ok("리포트 내용이 성공적으로 수정되었습니다.");
    }
}