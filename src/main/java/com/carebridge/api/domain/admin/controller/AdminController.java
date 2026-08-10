package com.carebridge.api.domain.admin.controller;

import com.carebridge.api.domain.admin.dto.request.MatchConfirmRequest;
import com.carebridge.api.domain.admin.dto.response.*;
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

    @GetMapping("/seniors/{seniorId}/recommends")
    public ResponseEntity<List<RecommendResponse>> getRecommends(@PathVariable Long seniorId) {
        return ResponseEntity.ok(adminService.getRecommendList(seniorId));
    }

    @PostMapping("/seniors/{seniorId}/match")
    public ResponseEntity<String> matchSenior(
            @PathVariable Long seniorId,
            @RequestBody MatchConfirmRequest request) {

        adminService.matchSenior(seniorId, request.getPartnerId());
        return ResponseEntity.ok("선택한 파트너와의 매칭이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/seniors/{seniorId}/unmatch")
    public ResponseEntity<String> unmatchSenior(@PathVariable Long seniorId) {
        adminService.unmatchSenior(seniorId);
        return ResponseEntity.ok("매칭이 성공적으로 해제되었습니다.");
    }

    @GetMapping("/dashboard/seniors")
    public ResponseEntity<List<SeniorDashboardResponse>> getDashboardSeniors() {
        return ResponseEntity.ok(adminService.getDashboardSeniors());
    }

    @GetMapping("/seniors/{seniorId}")
    public ResponseEntity<SeniorDetailResponse> getSeniorDetail(@PathVariable Long seniorId) {
        return ResponseEntity.ok(adminService.getSeniorDetail(seniorId));
    }

    @GetMapping("/exchange/{seniorId}/history")
    public ResponseEntity<List<ExchangeHistoryResponse>> getExchangeHistory(@PathVariable Long seniorId) {
        return ResponseEntity.ok(adminService.getExchangeHistory(seniorId));
    }

    @GetMapping("/missions/{missionId}/result")
    public ResponseEntity<MissionResultResponse> getMissionResult(@PathVariable Long missionId) {
        return ResponseEntity.ok(adminService.getMissionResult(missionId));
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(adminService.getDashboardSummary());
    }
}