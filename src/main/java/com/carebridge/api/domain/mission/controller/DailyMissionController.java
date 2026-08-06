package com.carebridge.api.domain.mission.controller;

import com.carebridge.api.domain.mission.dto.request.MissionCompleteRequest;
import com.carebridge.api.domain.mission.dto.request.TextMissionCompleteRequest;
import com.carebridge.api.domain.mission.dto.response.AiMissionEvaluationResponse;
import com.carebridge.api.domain.mission.dto.response.DailyMissionResponse;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/missions")
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;

    @GetMapping("/today")
    public ResponseEntity<List<DailyMissionResponse>> getTodayMissions(@RequestParam Long seniorId) {
        List<DailyMission> missions = dailyMissionService.getTodayMissions(seniorId);

        List<DailyMissionResponse> responseList = missions.stream()
                .map(DailyMissionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/{missionId}/complete")
    public ResponseEntity<String> completeMission(
            @PathVariable Long missionId,
            @RequestBody MissionCompleteRequest request) {

        dailyMissionService.completeMission(missionId, request.getAudioUrl(), request.getSttResult());
        return ResponseEntity.ok("미션이 완료되었습니다.");
    }

    @PostMapping("/{missionId}/complete/text")
    public ResponseEntity<AiMissionEvaluationResponse> completeTextMission(
            @PathVariable Long missionId,
            @RequestBody TextMissionCompleteRequest request) {

        AiMissionEvaluationResponse response = dailyMissionService.completeTextMission(missionId, request.getTextResult());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{missionId}/skip")
    public ResponseEntity<Map<String, String>> skipMission(@PathVariable Long missionId) {
        dailyMissionService.skipMission(missionId);

        return ResponseEntity.ok(Map.of("message", "미션을 건너뛰었습니다."));
    }
}