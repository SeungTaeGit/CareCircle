package com.carebridge.api.domain.mission.repository;

import com.carebridge.api.domain.mission.entity.ToxicExpressionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToxicExpressionLogRepository extends JpaRepository<ToxicExpressionLog, Long> {
}