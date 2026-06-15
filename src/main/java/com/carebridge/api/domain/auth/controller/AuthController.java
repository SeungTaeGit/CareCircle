package com.carebridge.api.domain.auth.controller;

import com.carebridge.api.domain.auth.dto.request.AdminLoginRequest;
import com.carebridge.api.domain.auth.dto.request.AdminSignupRequest;
import com.carebridge.api.domain.auth.dto.request.SeniorLoginRequest;
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
}