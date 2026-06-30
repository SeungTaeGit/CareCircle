package com.carebridge.api.domain.admin.service;

import com.carebridge.api.domain.admin.dto.response.SeniorListResponse;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SeniorRepository seniorRepository;

    @Transactional(readOnly = true)
    public List<SeniorListResponse> getSeniorList() {
        return seniorRepository.findAll().stream().map(senior -> {
            int age = 0;
            if (senior.getBirthDate() != null && senior.getBirthDate().length() >= 4) {
                age = LocalDate.now().getYear() - Integer.parseInt(senior.getBirthDate().substring(0, 4));
            }

            String partnerName = null;
            if ("MATCHED".equals(senior.getMatchStatus())) {
                Senior partner = seniorRepository.findByLinkCodeAndIdNot(senior.getLinkCode(), senior.getId())
                        .orElse(null);
                partnerName = (partner != null) ? partner.getName() + " (" + partner.getCountry() + ")" : "정보 없음";
            }

            return SeniorListResponse.builder()
                    .id(senior.getId())
                    .name(senior.getName())
                    .age(age)
                    .hobbies(senior.getHobbies())
                    .matchStatus(senior.getMatchStatus())
                    .partnerName(partnerName)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void matchSenior(Long seniorId) {
        Senior me = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다."));

        if ("MATCHED".equals(me.getMatchStatus())) {
            throw new IllegalStateException("이미 매칭된 어르신입니다.");
        }

        Senior partner = seniorRepository.findFirstByMatchStatusAndCountryNot("WAITING", me.getCountry())
                .orElseThrow(() -> new IllegalStateException("현재 매칭 가능한 해외 파트너가 없습니다. 잠시 후 다시 시도해주세요."));

        String newLinkCode = "MATCH-" + UUID.randomUUID().toString().substring(0, 8);

        me.updateMatchInfo("MATCHED", newLinkCode);
        partner.updateMatchInfo("MATCHED", newLinkCode);
    }

    @Transactional
    public void unmatchSenior(Long seniorId) {
        Senior me = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다."));

        if ("WAITING".equals(me.getMatchStatus())) {
            throw new IllegalStateException("이미 매칭 해제된(WAITING) 상태입니다.");
        }

        Senior partner = seniorRepository.findByLinkCodeAndIdNot(me.getLinkCode(), me.getId())
                .orElse(null);

        me.updateMatchInfo("WAITING", "WAIT-" + UUID.randomUUID().toString().substring(0, 6));

        if (partner != null) {
            partner.updateMatchInfo("WAITING", "WAIT-" + UUID.randomUUID().toString().substring(0, 6));
        }
    }
}