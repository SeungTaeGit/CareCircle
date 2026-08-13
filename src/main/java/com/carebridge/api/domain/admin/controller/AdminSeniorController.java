package com.carebridge.api.domain.admin.controller;

import com.carebridge.api.domain.admin.service.SeniorManagementService;
import com.carebridge.api.domain.senior.dto.response.SeniorDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/seniors")
@RequiredArgsConstructor
public class AdminSeniorController {

    private final SeniorManagementService seniorManagementService;

    @GetMapping("/detail/{seniorId}")
    public ResponseEntity<SeniorDetailResponse> getSeniorDetail(@PathVariable Long seniorId) {
        return ResponseEntity.ok(seniorManagementService.getSeniorDetail(seniorId));
    }
}