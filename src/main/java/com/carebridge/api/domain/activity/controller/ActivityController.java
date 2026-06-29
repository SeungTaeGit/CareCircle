package com.carebridge.api.domain.activity.controller;

import com.carebridge.api.domain.activity.dto.request.ActivitySaveRequest;
import com.carebridge.api.domain.activity.dto.response.ActivityRecordResponse;
import com.carebridge.api.domain.activity.dto.response.GuardianDashboardResponse;
import com.carebridge.api.domain.activity.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<String> saveActivity(
            Authentication authentication,
            @RequestPart(value = "data") ActivitySaveRequest request) {

        String seniorIdString = authentication.getName();

        activityService.saveActivity(seniorIdString, request);

        return ResponseEntity.ok("활동 기록이 성공적으로 저장되었습니다.");
    }

    @GetMapping("/guardian")
    public ResponseEntity<GuardianDashboardResponse> getGuardianDashboard(
            Authentication authentication
    ) {
        String guardianEmail = authentication.getName();

        GuardianDashboardResponse response = activityService.getGuardianDashboard(guardianEmail);


        return ResponseEntity.ok(response);
    }
}