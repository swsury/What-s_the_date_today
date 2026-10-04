package com.example.ddayapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.ddayapp.data.DDay
import com.example.ddayapp.ui.theme.toComposeColor
import com.example.ddayapp.utils.DateCalculator
import com.example.ddayapp.ui.theme.*
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.toArgb

// 디데이 카드

@Composable
fun DdayCard(
    dday: DDay,
    publicHolidays: Set<String>,
    customDays: Set<String>,
    onEdit: () -> Unit, // 편집
    onDuplicate: () -> Unit,  // 복제
    onDelete: () -> Unit, // 삭제
    modifier: Modifier = Modifier
) {
    // 더보기 메뉴 표시 여부
    var showMenu by remember { mutableStateOf(false) }

    // 디데이에 설정된 색상을 Compose Color로 변환 : 위젯 설정에도 사용됨
    val cardColor = dday.color.toComposeColor()

    // 디데이 계산 결과 문자열
    val ddayText = DateCalculator.calculateDDay(
        targetDate = dday.date, // 목표 날짜
        excludePublicHolidays = dday.excludePublicHolidays, // 공휴일 제외
        excludeCustomDays = dday.excludeCustomDays, // 휴무일 제외
        excludedWeekdays = dday.excludedWeekdays,  // 제외 요일
        publicHolidays = publicHolidays,
        customDays = customDays
    )

    // 날짜 형식 변환 함수
    val formattedDate = DateCalculator.formatDate(dday.date)

    // UI
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(
                        cardColor,
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 라벨명
                    Text(
                        text = dday.labelTitle,
                        color = White,
                        fontSize = 12.sp
                    )

                    // 우측 메뉴 버튼
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "메뉴",
                                tint = White
                            )
                        }

                        // 드롭다운 메뉴
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // 편집
                            DropdownMenuItem(
                                text = { Text("편집") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            // 복제
                            DropdownMenuItem(
                                text = { Text("복제") },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                }
                            )
                            // 삭제
                            DropdownMenuItem(
                                text = { Text("삭제", color = BasicSecondary) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            // 카드 본문
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 디데이 명, 디데이 일수
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 디데이 명
                    Text(
                        text = dday.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )

                    // 디데이 일수
                    Text(
                        text = ddayText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = cardColor
                    )
                }

                // 목표 날짜 표시
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = Gray
                    )
                }
            }
        }
    }
}