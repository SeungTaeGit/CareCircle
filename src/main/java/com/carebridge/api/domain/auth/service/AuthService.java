package com.carebridge.api.domain.auth.service;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.domain.admin.repository.AdminRepository;
import com.carebridge.api.domain.auth.dto.request.AdminSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signupAdmin(AdminSignupRequest request) {
        // 1. 이메일 중복 검사 (이미 가입된 기관인지 확인)
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Admin 객체(엔티티) 생성
        Admin admin = Admin.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .institutionName(request.getInstitutionName())
                .managerName(request.getManagerName())
                .build();

        // 4. DB에 저장하고, 저장된 관리자의 고유 ID(PK) 반환
        Admin savedAdmin = adminRepository.save(admin);
        return savedAdmin.getId();
    }
}