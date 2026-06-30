package com.carebridge.api.domain.admin.controller;

import com.carebridge.api.domain.admin.dto.response.SeniorListResponse;
import com.carebridge.api.domain.admin.service.AdminService;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SeniorRepository seniorRepository;
    private final AdminService adminService;

    @GetMapping("/seniors")
    public ResponseEntity<List<SeniorListResponse>> getSeniorList() {
        return ResponseEntity.ok(adminService.getSeniorList());
    }

    @PostMapping("/seniors/{seniorId}/match")
    public ResponseEntity<String> matchSenior(@PathVariable Long seniorId) {
        adminService.matchSenior(seniorId);
        return ResponseEntity.ok("매칭이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/seniors/{seniorId}/unmatch")
    public ResponseEntity<String> unmatchSenior(@PathVariable Long seniorId) {
        adminService.unmatchSenior(seniorId);
        return ResponseEntity.ok("매칭이 성공적으로 해제되었습니다.");
    }
}