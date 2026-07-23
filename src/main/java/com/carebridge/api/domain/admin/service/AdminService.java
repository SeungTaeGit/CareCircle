package com.carebridge.api.domain.admin.service;

import com.carebridge.api.domain.admin.dto.response.RecommendResponse;
import com.carebridge.api.domain.admin.dto.response.SeniorListResponse;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
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
                Senior partner = seniorRepository.findById(senior.getPartnerId())
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
    public void matchSenior(Long seniorId, Long partnerId) {
        Senior me = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다."));

        Senior partner = seniorRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("파트너 어르신을 찾을 수 없습니다."));

        if (!"WAITING".equals(me.getMatchStatus()) || !"WAITING".equals(partner.getMatchStatus())) {
            throw new IllegalStateException("매칭은 두 분 모두 'WAITING' 상태일 때만 가능합니다.");
        }

        me.updateMatchInfo("MATCHED", partner.getId());
        partner.updateMatchInfo("MATCHED", me.getId());
    }

    @Transactional
    public void unmatchSenior(Long seniorId) {
        Senior me = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다."));

        if ("WAITING".equals(me.getMatchStatus())) {
            throw new IllegalStateException("이미 매칭 해제된(WAITING) 상태입니다.");
        }

        if (me.getPartnerId() != null) {
            Senior partner = seniorRepository.findById(me.getPartnerId())
                    .orElse(null);

            if (partner != null) {
                partner.updateMatchInfo("WAITING", null);
            }
        }

        me.updateMatchInfo("WAITING", null);
    }

    @Transactional(readOnly = true)
    public List<RecommendResponse> getRecommendList(Long seniorId) {
        Senior me = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다."));

        List<Senior> candidates = seniorRepository.findAllByMatchStatusAndIdNot("WAITING", me.getId());

        return candidates.stream()
                .map(candidate -> {
                    int score = calculateMatchScore(me, candidate);
                    return RecommendResponse.builder()
                            .seniorId(candidate.getId())
                            .name(candidate.getName())
                            .country(candidate.getCountry())
                            .language(candidate.getLanguage())
                            .matchScore(score)
                            .build();
                })
                .sorted(Comparator.comparingInt(RecommendResponse::getMatchScore).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private int calculateMatchScore(Senior me, Senior candidate) {
        int score = 0;

        if (me.getCountry() != null && !me.getCountry().equals(candidate.getCountry())) {
            score += 50;
        }

        if (me.getLanguage() != null && me.getLanguage().equals(candidate.getLanguage())) {
            score += 30;
        }

        if (me.getHobbies() != null && candidate.getHobbies() != null) {
            if(me.getHobbies().equals(candidate.getHobbies())) {
                score += 10;
            }
        }

        return score;
    }
}