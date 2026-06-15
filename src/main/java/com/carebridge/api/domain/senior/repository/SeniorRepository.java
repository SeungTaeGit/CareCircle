package com.carebridge.api.domain.senior.repository;

import com.carebridge.api.domain.senior.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

    boolean existsByPinCode(String pinCode);
    boolean existsByLinkCode(String linkCode);
}