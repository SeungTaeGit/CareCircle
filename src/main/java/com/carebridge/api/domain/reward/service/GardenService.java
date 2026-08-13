package com.carebridge.api.domain.reward.service;

import com.carebridge.api.domain.reward.entity.Garden;
import com.carebridge.api.domain.reward.repository.GardenRepository;
import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.domain.senior.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GardenService {

    private final GardenRepository gardenRepository;
    private final SeniorRepository seniorRepository;

    @Transactional
    public void addExperiencePoint(Long seniorId, int exp) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                Senior senior = seniorRepository.findById(seniorId)
                        .orElseThrow(() -> new IllegalArgumentException("어르신을 찾을 수 없습니다."));

                Garden garden = gardenRepository.findBySeniorId(seniorId)
                        .orElseGet(() -> gardenRepository.save(Garden.builder().senior(senior).build()));

                garden.addExp(exp);

                log.info("🌱 {} 어르신의 정원에 {} XP가 추가되었습니다. (현재 레벨: {})", senior.getName(), exp, garden.getPlantLevel());
                return;

            } catch (ObjectOptimisticLockingFailureException e) {
                attempt++;
                log.warn("⚠️ 포인트 적립 충돌 감지! 재시도 중... (시도 {}/{})", attempt, maxRetries);

                if (attempt >= maxRetries) {
                    log.error("❌ 포인트 적립 최종 실패 (동시성 문제)");
                    throw new RuntimeException("포인트 적립 중 일시적인 오류가 발생했습니다. 다시 시도해주세요.");
                }

                try { Thread.sleep(50); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
    }

    @Transactional(readOnly = true)
    public com.carebridge.api.domain.reward.dto.response.GardenResponse getGardenInfo(Long seniorId) {
        return gardenRepository.findBySeniorId(seniorId)
                .map(g -> new com.carebridge.api.domain.reward.dto.response.GardenResponse(
                        g.getPlantLevel(),
                        g.getCurrentExp(),
                        g.getPlantLevel() * 100))
                .orElseGet(() -> new com.carebridge.api.domain.reward.dto.response.GardenResponse(1, 0, 100));
    }
}