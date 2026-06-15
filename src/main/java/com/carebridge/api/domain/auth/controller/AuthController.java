package com.carebridge.api.domain.auth.controller;

import com.carebridge.api.domain.auth.dto.request.*;
import com.carebridge.api.domain.auth.dto.response.TokenResponse;
import com.carebridge.api.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/signup")
    public ResponseEntity<String> signupAdmin(@RequestBody AdminSignupRequest request) {
        authService.signupAdmin(request);
        return ResponseEntity.ok("기관 관리자 회원가입이 완료되었습니다.");
    }

    @PostMapping("/admin/login")
    public ResponseEntity<TokenResponse> loginAdmin(@RequestBody AdminLoginRequest request) {
        String token = authService.loginAdmin(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/senior/login")
    public ResponseEntity<TokenResponse> loginSenior(@RequestBody SeniorLoginRequest request) {
        String token = authService.loginSenior(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/guardian/signup")
    public ResponseEntity<String> signupGuardian(@RequestBody GuardianSignupRequest request) {
        authService.signupGuardian(request);
        return ResponseEntity.ok("보호자 회원가입 및 어르신 연동이 완료되었습니다.");
    }

    @PostMapping("/guardian/login")
    public ResponseEntity<TokenResponse> loginGuardian(@RequestBody GuardianLoginRequest request) {
        String token = authService.loginGuardian(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}