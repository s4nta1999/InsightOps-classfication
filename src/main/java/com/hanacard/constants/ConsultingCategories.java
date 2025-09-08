package com.hanacard.constants;

import java.util.List;

/**
 * 상담 카테고리 상수
 */
public class ConsultingCategories {

    public static final List<String> CATEGORIES = List.of(
        "도난/분실 신청/해제",
        "이용내역 안내",
        "승인취소/매출취소 안내",
        "한도상향 접수/처리",
        "선결제/즉시출금",
        "한도 안내",
        "가상계좌 안내",
        "결제계좌 안내/변경",
        "서비스 이용방법 안내",
        "결제대금 안내",
        "연체대금 즉시출금",
        "포인트/마일리지 전환등록",
        "증명서/확인서 발급",
        "가상계좌 예약/취소",
        "단기카드대출 안내/실행",
        "장기카드대출 안내",
        "정부지원 바우처 (등유, 임신 등)",
        "이벤트 안내",
        "심사 진행사항 안내",
        "도시가스",
        "일부결제 대금이월약정 안내",
        "일부결제대금이월약정 해지",
        "결제일 안내/변경",
        "약관 안내",
        "상품 안내"
    );

    public static final int CATEGORY_COUNT = CATEGORIES.size();

    /**
     * 카테고리가 유효한지 확인
     */
    public static boolean isValidCategory(String category) {
        return CATEGORIES.contains(category);
    }

    /**
     * 모든 카테고리 반환
     */
    public static List<String> getAllCategories() {
        return List.copyOf(CATEGORIES);
    }
}
