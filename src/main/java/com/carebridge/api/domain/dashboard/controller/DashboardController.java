package com.carebridge.api.domain.dashboard.controller;

import com.carebridge.api.domain.dashboard.dto.response.EmotionChartResponse;
import com.carebridge.api.domain.dashboard.service.EmotionMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EmotionMonitoringService emotionMonitoringService;

    @GetMapping("/{seniorId}/emotions")
    public ResponseEntity<EmotionChartResponse> getEmotionChart(@PathVariable Long seniorId) {
        EmotionChartResponse response = emotionMonitoringService.getEmotionChart(seniorId);
        return ResponseEntity.ok(response);
    }
}