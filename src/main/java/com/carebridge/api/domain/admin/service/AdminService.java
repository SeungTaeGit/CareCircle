package com.carebridge.api.domain.admin.service;

import com.carebridge.api.domain.admin.dto.response.*;
import com.carebridge.api.domain.exchange.repository.ExchangeMessageRepository;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SeniorRepository seniorRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final ExchangeMessageRepository exchangeMessageRepository;

    @Transactional(readOnly = true)
    public List<SeniorListResponse> getSeniorList() {
        return seniorRepository.findAll().stream().map(senior -> {
            int age = 0;
            if (senior.getBirthDate() != null && senior.getBirthDate().length() >= 4) {
                age = LocalDate.now().getYear() - Integer.parseInt(senior.getBirthDate().substring(0, 4));
            }

            String partnerName = null;
            if ("MATCHED".equals(senior.getMatchStatus()) && senior.getPartnerId() != null) {
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
                    .country(senior.getCountry())
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

    @Transactional(readOnly = true)
    public List<SeniorDashboardResponse> getDashboardSeniors() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = today.with(java.time.DayOfWeek.SUNDAY).atTime(23, 59, 59);

        return seniorRepository.findAll().stream().map(senior -> {
            int age = 0;
            if (senior.getBirthDate() != null && senior.getBirthDate().length() >= 4) {
                age = today.getYear() - Integer.parseInt(senior.getBirthDate().substring(0, 4));
            }

            List<DailyMission> thisWeekMissions = dailyMissionRepository
                    .findBySeniorIdAndAssignedAtBetween(senior.getId(), startOfWeek, endOfWeek);

            int completedCount = (int) thisWeekMissions.stream()
                    .filter(m -> m.getStatus() == com.carebridge.api.domain.mission.enums.MissionStatus.COMPLETED)
                    .count();

            List<String> emotions = thisWeekMissions.stream()
                    .filter(m -> m.getEmotion() != null)
                    .map(DailyMission::getEmotion)
                    .limit(5)
                    .collect(Collectors.toList());

            return SeniorDashboardResponse.builder()
                    .seniorId(senior.getId())
                    .name(senior.getName())
                    .gender(senior.getGender())
                    .age(age)
                    .lastActiveAt(senior.getLastActiveAt())
                    .thisWeekCompletedCount(completedCount)
                    .thisWeekTotalCount(thisWeekMissions.size())
                    .recentEmotions(emotions)
                    .interestLevel(senior.getInterestLevel())
                    .recommendedAction(senior.getRecommendedAction())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SeniorDetailResponse getSeniorDetail(Long seniorId) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다. ID: " + seniorId));

        return SeniorDetailResponse.from(senior);
    }

    @Transactional(readOnly = true)
    public List<ExchangeHistoryResponse> getExchangeHistory(Long seniorId) {
        return exchangeMessageRepository.findAllBySenderIdOrReceiverIdOrderByCreatedAtDesc(seniorId, seniorId)
                .stream()
                .map(ExchangeHistoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MissionResultResponse getMissionResult(Long missionId) {
        DailyMission mission = dailyMissionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 미션을 찾을 수 없습니다. ID: " + missionId));

        return MissionResultResponse.from(mission);
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<DailyMission> todayMissions = dailyMissionRepository.findByAssignedAtBetween(startOfDay, endOfDay);

        int total = todayMissions.size();
        int completed = (int) todayMissions.stream()
                .filter(m -> m.getStatus() == com.carebridge.api.domain.mission.enums.MissionStatus.COMPLETED)
                .count();

        double rate = (total == 0) ? 0.0 : Math.round(((double) completed / total) * 1000.0) / 10.0;

        return DashboardSummaryResponse.builder()
                .todayTotalMissions(total)
                .todayCompletedMissions(completed)
                .participationRate(rate)
                .build();
    }
}