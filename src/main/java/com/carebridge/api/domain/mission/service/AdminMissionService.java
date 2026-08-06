package com.carebridge.api.domain.mission.service;

import com.carebridge.api.domain.mission.dto.request.AdminMissionCreateRequest;
import com.carebridge.api.domain.mission.dto.response.AdminMissionResponse;
import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.repository.DailyMissionRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMissionService {

    private final DailyMissionRepository dailyMissionRepository;
    private final SeniorRepository seniorRepository;

    @Transactional
    public Long createMission(AdminMissionCreateRequest request) {
        Senior senior = seniorRepository.findById(request.getSeniorId())
                .orElseThrow(() -> new IllegalArgumentException("해당 어르신을 찾을 수 없습니다. ID: " + request.getSeniorId()));

        DailyMission dailyMission = DailyMission.builder()
                .senior(senior)
                .missionTemplate(null)
                .customTitle("관리자 발급 미션")
                .customContent(request.getContent())
                .build();

        DailyMission savedMission = dailyMissionRepository.save(dailyMission);
        return savedMission.getId();
    }

    public List<AdminMissionResponse> getAllMissions(Long seniorId) {
        List<DailyMission> missions;

        if (seniorId != null) {
            missions = dailyMissionRepository.findBySeniorId(seniorId);
        } else {
            missions = dailyMissionRepository.findAll();
        }

        return missions.stream()
                .map(AdminMissionResponse::from)
                .collect(Collectors.toList());
    }
}