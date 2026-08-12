package com.carebridge.api.domain.report.enums;

public enum ReportStatus {
    PENDING, // AI가 초안을 작성하여 관리자 검수를 대기 중인 상태
    SENT     // 관리자가 확인 후 보호자에게 성공적으로 전송한 상태
}