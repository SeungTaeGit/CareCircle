package com.carebridge.api.domain.notification.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SopRule {

    COGNITIVE_DROP(
            "인지 점수 급감 감지",
            "1단계: 어르신의 최근 수면 상태나 컨디션 저하 여부를 가볍게 질문하세요.\n2단계: 지속적인 하락 시, 보건소 치매안심센터 CIST 검사를 권고하세요."
    ),

    EXCHANGE_ISOLATION(
            "48시간 응답 지연 감지",
            "1단계: 글로벌 파트너의 메시지가 도착했음을 구두로 안내하고 함께 화면을 터치해 보세요.\n2단계: 어르신이 교류에 흥미를 잃었다면, 기관 내 그룹 매칭으로 모드를 변경해 보세요."
    ),

    NEGATIVE_EXPRESSION(
            "부정 감정 키워드 감지",
            "1단계: 가벼운 티타임을 통해 어르신의 최근 고민을 들어주세요.\n2단계: 우울감이 깊어 보일 경우, 보호자 앱을 통해 상황을 공유하세요."
    );

    private final String message;
    private final String guide;
}