package com.carebridge.api.domain.guardian.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guardians")
@RequiredArgsConstructor
public class GuardianController {

    // private final GuardianService guardianService;

    @GetMapping("/seniors/{seniorId}/status")
    public ResponseEntity<?> getSeniorStatus(@PathVariable Long seniorId) {
        return ResponseEntity.ok().build();
    }
}