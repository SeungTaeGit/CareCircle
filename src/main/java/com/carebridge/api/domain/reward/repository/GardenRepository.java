package com.carebridge.api.domain.reward.repository;

import com.carebridge.api.domain.reward.entity.Garden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GardenRepository extends JpaRepository<Garden, Long> {
    Optional<Garden> findBySeniorId(Long seniorId);
}