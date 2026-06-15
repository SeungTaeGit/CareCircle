package com.carebridge.api.domain.senior.controller;

import com.carebridge.api.domain.senior.dto.request.SeniorCreateRequest;
import com.carebridge.api.domain.senior.dto.response.SeniorCreateResponse;
import com.carebridge.api.domain.senior.service.SeniorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seniors")
@RequiredArgsConstructor
public class SeniorController {

    private final SeniorService seniorService;

    @PostMapping
    public ResponseEntity<SeniorCreateResponse> registerSenior(
            Authentication authentication,
            @RequestBody SeniorCreateRequest request) {

        String adminEmail = authentication.getName();

        SeniorCreateResponse response = seniorService.registerSenior(adminEmail, request);

        return ResponseEntity.ok(response);
    }
}