package com.carebridge.api.domain.admin.service;

import com.carebridge.api.domain.guardian.entity.Guardian;
import com.carebridge.api.domain.guardian.repository.GuardianRepository;
import com.carebridge.api.domain.senior.dto.response.SeniorDetailResponse;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeniorManagementService {

    private final SeniorRepository seniorRepository;
    private final GuardianRepository guardianRepository;

    @Transactional(readOnly = true)
    public SeniorDetailResponse getSeniorDetail(Long seniorId) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("어르신을 찾을 수 없습니다."));

        List<Guardian> guardians = guardianRepository.findBySeniorId(seniorId);

        List<SeniorDetailResponse.GuardianInfo> guardianInfos = guardians.stream()
                .map(g -> SeniorDetailResponse.GuardianInfo.builder()
                        .name(g.getName())
                        .phoneNumber(g.getPhoneNumber())
                        .email(g.getEmail())
                        .build())
                .collect(Collectors.toList());

        return SeniorDetailResponse.builder()
                .seniorId(senior.getId())
                .name(senior.getName())
                .contact(senior.getContact())
                .gender(senior.getGender())
                .birthDate(senior.getBirthDate())
                .country(senior.getCountry())
                .language(senior.getLanguage())
                .matchStatus(senior.getMatchStatus())
                .hobbies(senior.getHobbies())
                .xp(senior.getXp())
                .interestLevel(senior.getInterestLevel().name())
                .recommendedAction(senior.getRecommendedAction())
                .guardians(guardianInfos)
                .build();
    }
}