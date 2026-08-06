package com.carebridge.api.domain.mission.controller;

import com.carebridge.api.domain.mission.dto.request.AdminMissionCreateRequest;
import com.carebridge.api.domain.mission.dto.response.AdminMissionCreateResponse;
import com.carebridge.api.domain.mission.dto.response.AdminMissionResponse;
import com.carebridge.api.domain.mission.service.AdminMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/missions")
public class AdminMissionController {

    private final AdminMissionService adminMissionService;

    @PostMapping
    public ResponseEntity<AdminMissionCreateResponse> createMission(
            @RequestBody AdminMissionCreateRequest request) {

        Long missionId = adminMissionService.createMission(request);
        return ResponseEntity.ok(new AdminMissionCreateResponse(missionId));
    }

    @GetMapping
    public ResponseEntity<List<AdminMissionResponse>> getMissions(
            @RequestParam(required = false) Long seniorId) {

        List<AdminMissionResponse> responseList = adminMissionService.getAllMissions(seniorId);
        return ResponseEntity.ok(responseList);
    }
}