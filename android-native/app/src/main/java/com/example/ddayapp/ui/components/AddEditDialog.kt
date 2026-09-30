package com.example.ddayapp.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import com.example.ddayapp.data.DDay
import com.example.ddayapp.ui.theme.toComposeColor
import com.example.ddayapp.utils.DateCalculator
import java.util.Calendar

// 디데이 추가, 수정

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDialog(
    dday: DDay?, // 추가 모드일 경우 null, 수정 모드 일 경우 기존 디데이 객체
    publicHolidays:  Set<String>, // 공휴일 날짜 목록
    customDays: Set<String>, // 휴무일 날짜 목록
    onDismiss: () -> Unit, // 다이얼로그 닫기
    onSave: (DDay) -> Unit // 저장 버튼 클릭 시 디데이 호출해서 반환
) {

    // state 변수

    // 라벨 명, 디데이명, 목표 날짜 설정
    // remember : recomposition 시 값 유지
    // 기존 디데이가 있으면 값 로드, 없으면 기본값 사용
    var labelTitle by remember { mutableStateOf(dday?.labelTitle ?: "") }
    var title by remember { mutableStateOf(dday?.title ?: "") }
    var date by remember { mutableStateOf(dday?.date ?: DateCalculator.getTodayString()) }

    // 색상 설정
    var selectedColor by remember { mutableStateOf(dday?.color ?: "#468BD7") }
    val colors = listOf(
        "#468BD7", "#218efd", "#ff6b6b",
        "#a855f7", "#f59e0b", "#10b981"
    )

    // 제외일 설정
    var excludePublicHolidays by remember { mutableStateOf(dday?.excludePublicHolidays ?: false) } // 공휴일 제외
    var excludeCustomDays by remember { mutableStateOf(dday?.excludeCustomDays ?: false) } // 휴무일 제외
    var excludedWeekdays by remember { mutableStateOf(dday?.excludedWeekdays ?: emptySet()) }  // 제외 요일 설정

    // 달력 창
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // 요일 설정
    val weekdays = listOf(
        1 to "일",  // SUNDAY
        2 to "월",  // MONDAY
        3 to "화",  // TUESDAY
        4 to "수",  // WEDNESDAY
        5 to "목",  // THURSDAY
        6 to "금",  // FRIDAY
        7 to "토"   // SATURDAY
    )

    // UI
    /*
    Dialog : 모달 팝업
    Card : 둥근 모서리 + 배경
    Column + verticalScroll : 작은 화면에서도 스크롤 가능하게 설정
     */

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // 헤터

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (dday == null) "새 D-day 추가" else "D-day 편집",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }

                Spacer(Modifier.height(24.dp)) // 구분 공간

                // 라벨 명

                Text("라벨", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = labelTitle,
                    onValueChange = { labelTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("라벨을 입력하세요") },
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // 디데이 명

                Text("제목", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("제목을 입력하세요") },
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // 목표 날짜 입력

                Text("날짜", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    placeholder = { Text("yyyy-MM-dd") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray
                    )
                )

                // 달력 모달 창

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val cal = Calendar.getInstance().apply {
                                        timeInMillis = millis
                                    }
                                    date = String.format(
                                        "%04d-%02d-%02d",
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH) + 1,
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    )
                                }
                                showDatePicker = false
                            }) {
                                Text("확인")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("취소")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 휴일 제외 옵션 선택

                Text("휴일 제외", fontSize = 14.sp, color = Color(0xFF666666))

                // 공휴일 제외 선택

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = excludePublicHolidays,
                        onCheckedChange = { excludePublicHolidays = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "공휴일 제외 (${publicHolidays.size}개)",
                        fontSize = 14.sp,
                        color = Color(0xFFAACBE0),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 휴무일 제외 선택

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = excludeCustomDays,
                        onCheckedChange = { excludeCustomDays = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "휴무일 제외 (${customDays.size}개)",
                        fontSize = 14.sp,
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold
                    )
                }

                // 공휴일, 휴무일 제외 안내 문구

                Text(
                    text = when {
                        excludePublicHolidays && excludeCustomDays ->
                            "공휴일과 휴무일을 제외하고 카운트합니다"
                        excludePublicHolidays ->
                            "공휴일을 제외하고 카운트합니다"
                        excludeCustomDays ->
                            "휴무일을 제외하고 카운트합니다"
                        else ->
                            "모든 날짜를 포함합니다"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp)
                )

                Spacer(Modifier.height(16.dp))

                // 제외 요일 선택

                Text("선택 요일 제외", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(8.dp))

                // 버튼 형태로 제외 요일 선택
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 첫 번째 줄: 일, 월, 화, 수
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        weekdays.take(4).forEach { (dayOfWeek, dayName) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        color = when {
                                            dayOfWeek == 1 && excludedWeekdays.contains(dayOfWeek) -> Color(0xFFFF5252) // 일요일 선택
                                            dayOfWeek == 7 && excludedWeekdays.contains(dayOfWeek) -> Color(0xFF2196F3) // 토요일 선택
                                            excludedWeekdays.contains(dayOfWeek) -> Color(0xFF26A69A) // 평일 선택
                                            else -> Color(0xFFE0E0E0) // 미선택
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        excludedWeekdays = if (excludedWeekdays.contains(dayOfWeek)) {
                                            excludedWeekdays - dayOfWeek
                                        } else {
                                            excludedWeekdays + dayOfWeek
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (excludedWeekdays.contains(dayOfWeek))
                                        Color.White
                                    else
                                        Color(0xFF999999)
                                )
                            }
                        }
                    }

                    // 두 번째 줄 : 목, 금, 토
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        weekdays.drop(4).forEach { (dayOfWeek, dayName) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        color = when {
                                            dayOfWeek == 1 && excludedWeekdays.contains(dayOfWeek) -> Color(0xFFFF5252) // 일요일 선택
                                            dayOfWeek == 7 && excludedWeekdays.contains(dayOfWeek) -> Color(0xFF2196F3) // 토요일 선택
                                            excludedWeekdays.contains(dayOfWeek) -> Color(0xFF26A69A) // 평일 선택
                                            else -> Color(0xFFE0E0E0) // 미선택
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        excludedWeekdays = if (excludedWeekdays.contains(dayOfWeek)) {
                                            excludedWeekdays - dayOfWeek
                                        } else {
                                            excludedWeekdays + dayOfWeek
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (excludedWeekdays.contains(dayOfWeek))
                                        Color.White
                                    else
                                        Color(0xFF999999)
                                )
                            }
                        }
                        // 빈 공간 채우기
                        Spacer(Modifier.weight(1f))
                    }
                }

                // 제외 요일 안내 문구

                Text(
                    text = when {
                        excludedWeekdays.isEmpty() -> "제외할 요일이 없습니다"
                        excludedWeekdays.size == 7 -> "모든 요일을 제외합니다"
                        else -> {
                            val selectedDays = weekdays
                                .filter { excludedWeekdays.contains(it.first) }
                                .joinToString(", ") { it.second }
                            "$selectedDays 요일을 제외합니다"
                        }
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )

                Spacer(Modifier.height(16.dp))

                // 디데이 카드 색상 설정
                // 색상 값은 colors 함수에 저장

                Text("색상", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color.toComposeColor())
                                .border(
                                    width = if (color == selectedColor) 3.dp else 0.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // 저장

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            return@Button
                        }

                        onSave(
                            DDay(
                                id = dday?.id ?: System.currentTimeMillis(),
                                labelTitle = labelTitle,
                                title = title,
                                date = date,
                                color = selectedColor,
                                excludePublicHolidays = excludePublicHolidays,
                                excludeCustomDays = excludeCustomDays,
                                excludedWeekdays = excludedWeekdays
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedColor.toComposeColor()
                    )
                ) {
                    Text(
                        text = if (dday == null) "추가" else "수정",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}