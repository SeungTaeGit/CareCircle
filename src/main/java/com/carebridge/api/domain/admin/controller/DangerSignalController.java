package com.carebridge.api.domain.admin.controller;

import com.carebridge.api.domain.admin.dto.response.DangerSignalResponse;
import com.carebridge.api.domain.admin.service.DangerSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/signals")
@RequiredArgsConstructor
public class DangerSignalController {

    private final DangerSignalService dangerSignalService;

    @GetMapping
    public ResponseEntity<List<DangerSignalResponse>> getPendingSignals() {
        List<DangerSignalResponse> responses = dangerSignalService.getPendingSignals();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{signalId}/resolve")
    public ResponseEntity<Void> resolveSignal(@PathVariable Long signalId) {
        dangerSignalService.resolveSignal(signalId);
        return ResponseEntity.ok().build();
    }
}