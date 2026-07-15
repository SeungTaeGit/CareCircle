package com.carebridge.api.domain.admin.dto.response;

import com.carebridge.api.domain.admin.entity.DangerSignal;
import java.time.LocalDateTime;

public record DangerSignalResponse(
        Long signalId,
        Long seniorId,
        String seniorName,
        String dangerType,
        String description,
        LocalDateTime createdAt
) {
    public static DangerSignalResponse from(DangerSignal signal) {
        return new DangerSignalResponse(
                signal.getId(),
                signal.getSenior().getId(),
                signal.getSenior().getName(),
                signal.getDangerType().name(),
                signal.getDescription(),
                signal.getCreatedAt()
        );
    }
}