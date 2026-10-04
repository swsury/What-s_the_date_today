package com.example.ddayapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// 색상 설정
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Gray = Color(0xFF808080)

// 기본 색상
val BasicPrimary = Color(0xFF9CAF88)
val BasicSecondary = Color(0xFFFABC9F)
val BasicTertiary = Color(0xFFA1DAEA)
val BasicQuinary = Color(0xFFE0E0E0)

// 디데이 색상 팔레트
val DdayPicker1 = Color(0xFFFFABAB)
val DdayPicker2 = Color(0xFFFFDAAB)
val DdayPicker3 = Color(0xFFDDFFAB)
val DdayPicker4 = Color(0xFFABE4FF)
val DdayPicker5 = Color(0xFFD9ABFF)

// 배경
val BackgroundGray = Color(0xFFF8F9FA)
val BackgroundBlue = Color(0xFF468BD7)
val BackgroundRed = Color(0xFFFFF5F5)
val BackgroundWhite = Color(0xFFF0F9F8)

// 글자
val TextBlack = Color(0xFF000000)
val TextGray = Color(0xFF999999)
val TextPrimary = Color(0xFF1B1C1F)
val TextSecondary = Color(0xFF666666)

// 디데이 선택 가능한 Color 팔레트 리스트
val ddayColorPalette = listOf(
    DdayPicker1, DdayPicker2,
    DdayPicker3, DdayPicker4, DdayPicker5
)

// Hex 문자열 > Color 변환
fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        BasicPrimary
    }
}

// Compose Color -> Hex 문자열 변환 (데이터 저장 시 사용)
fun Color.toHexString(): String {
    val argb = this.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}
