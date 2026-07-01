package com.carebridge.api.domain.senior.service;

import com.carebridge.api.domain.admin.entity.Admin;
import com.carebridge.api.domain.admin.repository.AdminRepository;
import com.carebridge.api.domain.senior.dto.request.SeniorCreateRequest;
import com.carebridge.api.domain.senior.dto.response.PartnerProfileResponse;
import com.carebridge.api.domain.senior.dto.response.SeniorCreateResponse;
import com.carebridge.api.domain.senior.dto.response.SeniorProfileResponse;
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
                .contact(request.getContact())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .country(request.getCountry())
                .language(request.getLanguage())
                .hobbies(request.getHobbies())
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

    @Transactional(readOnly = true)
    public SeniorProfileResponse getMyProfile(Long seniorId) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return new SeniorProfileResponse(senior);
    }

    @Transactional(readOnly = true)
    public PartnerProfileResponse getPartnerProfile(Long myId) {
        Senior me = seniorRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (me.getLinkCode() == null || !"MATCHED".equals(me.getMatchStatus())) {
            throw new IllegalStateException("아직 매칭된 파트너가 없습니다.");
        }

        Senior partner = seniorRepository.findByLinkCodeAndIdNot(me.getLinkCode(), me.getId())
                .orElseThrow(() -> new IllegalStateException("파트너 정보를 찾을 수 없습니다."));

        return PartnerProfileResponse.builder()
                .partnerId(partner.getId())
                .partnerName(partner.getName())
                .country(partner.getCountry())
                .language(partner.getLanguage())
                .build();
    }
}