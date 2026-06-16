package com.carebridge.api.domain.exchange.repository;

import com.carebridge.api.domain.exchange.entity.ExchangeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeMessageRepository extends JpaRepository<ExchangeMessage, Long> {
    List<ExchangeMessage> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}