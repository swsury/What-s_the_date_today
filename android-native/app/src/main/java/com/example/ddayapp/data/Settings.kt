package com.example.ddayapp.data

// 애플리케이션에서 사용되는 설정 정보를 담는 데이터 모델
data class Settings(
    val publicHolidays: List<Holiday> = emptyList(),  //  공휴일 : 자동 - 한국 공휴일 API 사용
    val customDays: List<Holiday> = emptyList(),      //  휴무일 : 수동 - 사용자가 추가하는 휴일 저장 데이터
    val sabbathDay: String? = null // 제외 요일 : 수동 - 제외 요일 선택 시 저장되는 데이터
)