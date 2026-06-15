package com.carebridge.api.domain.auth.service;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.domain.admin.repository.AdminRepository;
import com.carebridge.api.domain.auth.dto.request.AdminSignupRequest;
import com.carebridge.api.domain.auth.dto.request.SeniorLoginRequest;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import com.carebridge.api.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carebridge.api.domain.auth.dto.request.AdminLoginRequest;
import com.carebridge.api.global.config.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SeniorRepository seniorRepository;

    @Transactional
    public Long signupAdmin(AdminSignupRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .institutionName(request.getInstitutionName())
                .managerName(request.getManagerName())
                .build();

        Admin savedAdmin = adminRepository.save(admin);
        return savedAdmin.getId();
    }

    @Transactional(readOnly = true)
    public String loginAdmin(AdminLoginRequest request) {

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.createToken(admin.getEmail(), "ROLE_ADMIN");
    }

    @Transactional(readOnly = true)
    public String loginSenior(SeniorLoginRequest request) {

        Senior senior = seniorRepository.findByPinCode(request.getPinCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 핀 번호입니다."));

        return jwtProvider.createToken(String.valueOf(senior.getId()), "ROLE_SENIOR");
    }
}