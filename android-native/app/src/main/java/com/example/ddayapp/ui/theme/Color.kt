package com.example.ddayapp.ui.theme

import androidx.compose.ui.graphics.Color

// 색상 설정

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// 디데이 색상 팔레트
val DdayTeal = Color(0xFFAACBE0)
val DdayBlue = Color(0xFF218efd)
val DdayRed = Color(0xFFff6b6b)
val DdayPurple = Color(0xFFa855f7)
val DdayOrange = Color(0xFFf59e0b)
val DdayGreen = Color(0xFF10b981)

// 배경 및 기타 색상
val BackgroundGray = Color(0xFFF8F9FA)
val DividerGray = Color(0xFFE0E5ED)
val TextPrimary = Color(0xFF1B1C1F)
val TextSecondary = Color(0xFF666666)

// Hex 문자열 > Color 변환
fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        DdayTeal
    }
}
