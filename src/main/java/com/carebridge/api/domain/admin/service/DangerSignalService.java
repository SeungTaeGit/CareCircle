package com.carebridge.api.domain.admin.service;

import com.carebridge.api.domain.admin.dto.response.DangerSignalResponse;
import com.carebridge.api.domain.admin.entity.DangerSignal;
import com.carebridge.api.domain.admin.entity.enums.DangerStatus;
import com.carebridge.api.domain.admin.repository.DangerSignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DangerSignalService {

    private final DangerSignalRepository dangerSignalRepository;

    public List<DangerSignalResponse> getPendingSignals() {
        return dangerSignalRepository.findAllByStatusOrderByCreatedAtDesc(DangerStatus.PENDING)
                .stream()
                .map(DangerSignalResponse::from)
                .toList();
    }

    @Transactional
    public void resolveSignal(Long signalId) {
        DangerSignal signal = dangerSignalRepository.findById(signalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 위험 신호를 찾을 수 없습니다: " + signalId));

        signal.resolve();
    }
}