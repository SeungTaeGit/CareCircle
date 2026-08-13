package com.carebridge.api.domain.message.repository;

import com.carebridge.api.domain.message.entity.CheerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CheerMessageRepository extends JpaRepository<CheerMessage, Long> {
    List<CheerMessage> findBySeniorIdOrderByCreatedAtDesc(Long seniorId);
}