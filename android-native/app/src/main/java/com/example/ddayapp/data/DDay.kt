package com.example.ddayapp.data

// 애플리케이션에서 사용되는 개별 디데이 정보를 담는 데이터 모델
// 각 디데이별로 적용
data class DDay(
    val id: Long, // 디데이 ID (식별용 기본키)
    val title: String, // 디데이 이름
    val labelTitle: String, // 디데이 분류 라벨
    val date: String, // 목표 날짜
    val color: String, // 디데이 색상

    val excludePublicHolidays: Boolean, // 공휴일 제외 여부
    val excludeCustomDays: Boolean, // 휴무일 제외 여부
    val excludedWeekdays: Set<Int> = emptySet(), // 제외 요일, 기본값은 비어 있는 세트로 모든 요일을 포함하여 계산

    val order: Int = 0 // 오름차순 정렬, 숫자가 작을수록 목록 위쪽에 표시
)