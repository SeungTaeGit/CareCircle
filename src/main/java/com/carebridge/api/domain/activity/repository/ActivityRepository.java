package com.carebridge.api.domain.activity.repository;

import com.carebridge.api.domain.activity.entity.ActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<ActivityRecord, Long> {
}