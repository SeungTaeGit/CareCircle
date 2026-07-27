package com.carebridge.api.domain.mission.repository;

import com.carebridge.api.domain.mission.entity.MissionTemplate;
import com.carebridge.api.domain.mission.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionTemplateRepository extends JpaRepository<MissionTemplate, Long> {

    List<MissionTemplate> findByType(MissionType type);
}