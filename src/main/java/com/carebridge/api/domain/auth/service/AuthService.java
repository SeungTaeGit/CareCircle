package com.carebridge.api.domain.auth.service;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.domain.admin.repository.AdminRepository;
import com.carebridge.api.domain.auth.dto.request.*;
import com.carebridge.api.domain.guardian.entity.Guardian;
import com.carebridge.api.domain.guardian.repository.GuardianRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import com.carebridge.api.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carebridge.api.global.config.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final GuardianRepository guardianRepository;
    private final SeniorRepository seniorRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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

        senior.updateLastActiveAt();

        return jwtProvider.createToken(String.valueOf(senior.getId()), "ROLE_SENIOR");
    }

    @Transactional
    public void signupGuardian(GuardianSignupRequest request) {

        if (guardianRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Senior senior = seniorRepository.findByLinkCode(request.getLinkCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 어르신 연동 코드입니다."));

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Guardian guardian = Guardian.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .senior(senior)
                .build();

        guardianRepository.save(guardian);
    }

    @Transactional(readOnly = true)
    public String loginGuardian(GuardianLoginRequest request) {

        Guardian guardian = guardianRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), guardian.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.createToken(guardian.getEmail(), "ROLE_GUARDIAN");
    }
}