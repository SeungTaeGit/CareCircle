package com.carebridge.api.domain.senior.repository;

import com.carebridge.api.domain.senior.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

    boolean existsByPinCode(String pinCode);
    boolean existsByLinkCode(String linkCode);

    Optional<Senior> findByPinCode(String pinCode);
    Optional<Senior> findByLinkCode(String linkCode);
    Optional<Senior> findByLinkCodeAndIdNot(String linkCode, Long myId);
    Optional<Senior> findFirstByMatchStatusAndCountryNot(String matchStatus, String country);
}