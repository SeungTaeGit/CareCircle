package com.carebridge.api.domain.senior.service;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.domain.admin.repository.AdminRepository;
import com.carebridge.api.domain.senior.dto.request.SeniorCreateRequest;
import com.carebridge.api.domain.senior.dto.response.SeniorCreateResponse;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import com.carebridge.api.global.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeniorService {

    private final SeniorRepository seniorRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public SeniorCreateResponse registerSenior(String adminEmail, SeniorCreateRequest request) {

        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

        String uniquePinCode = createUniquePinCode();
        String uniqueLinkCode = createUniqueLinkCode();

        Senior senior = Senior.builder()
                .name(request.getName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .pinCode(uniquePinCode)
                .linkCode(uniqueLinkCode)
                .admin(admin)
                .build();

        seniorRepository.save(senior);

        return new SeniorCreateResponse(senior.getName(), senior.getPinCode(), senior.getLinkCode());
    }

    private String createUniquePinCode() {
        String pin;
        do {
            pin = CodeGenerator.generatePinCode();
        } while (seniorRepository.existsByPinCode(pin));
        return pin;
    }

    private String createUniqueLinkCode() {
        String code;
        do {
            code = CodeGenerator.generateLinkCode();
        } while (seniorRepository.existsByLinkCode(code));
        return code;
    }
}