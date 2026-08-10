package com.carebridge.api.domain.mission.repository;

import com.carebridge.api.domain.mission.entity.DailyMission;
import com.carebridge.api.domain.mission.enums.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    List<DailyMission> findBySeniorId(Long seniorId);

    List<DailyMission> findBySeniorIdAndStatus(Long seniorId, MissionStatus status);

    List<DailyMission> findBySeniorIdAndAssignedAtBetween(Long seniorId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<DailyMission> findTop3BySeniorIdOrderByAssignedAtDesc(Long seniorId);

    List<DailyMission> findByAssignedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}