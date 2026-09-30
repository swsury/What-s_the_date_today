package com.example.ddayapp.data

// 애플리케이션에서 사용되는 개별 휴일 정보를 담는 데이터 모델
data class Holiday(
    val date: String, // 휴일 날짜, format : "yyyy-MM-dd"
    val name: String = "" // 휴일 이름
)
