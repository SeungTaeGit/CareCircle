package com.carebridge.api.domain.guardian.repository;

import com.carebridge.api.domain.guardian.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    boolean existsByEmail(String email);

    Optional<Guardian> findByEmail(String email);
}