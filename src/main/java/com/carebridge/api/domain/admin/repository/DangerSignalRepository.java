package com.carebridge.api.domain.admin.repository;

import com.carebridge.api.domain.admin.entity.DangerSignal;
import com.carebridge.api.domain.admin.entity.enums.DangerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DangerSignalRepository extends JpaRepository<DangerSignal, Long> {

    List<DangerSignal> findAllByStatusOrderByCreatedAtDesc(DangerStatus status);

    List<DangerSignal> findAllBySeniorIdOrderByCreatedAtDesc(Long seniorId);
}