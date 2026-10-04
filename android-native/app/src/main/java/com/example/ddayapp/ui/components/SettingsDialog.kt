package com.example.ddayapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ddayapp.data.Holiday
import com.example.ddayapp.data.Settings
import com.example.ddayapp.utils.DateCalculator
import android.graphics.Color as AndroidColor
import com.example.ddayapp.ui.theme.*
import java.util.*

// 휴일 설정
// 공휴일 : 자동, 휴무일 : 사용자 추가 / 삭제

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    settings: Settings, // 기존 설정 값
    isLoadingHolidays: Boolean, // 공휴일 로딩 상태
    onDismiss: () -> Unit, // 다이얼로그 닫기
    onSave: (Settings) -> Unit, // 저장
    onAddCustomDay: (Holiday) -> Unit, // 휴무일 추가
    onRemoveCustomDay: (String) -> Unit // 휴무일 삭제
) {

    // 상태
    var publicHolidays by remember { mutableStateOf<List<Holiday>>(settings.publicHolidays) }  // 공휴일 목록
    var customDays by remember { mutableStateOf<List<Holiday>>(settings.customDays) }  // 휴무일 목록
    var sabbathDay by remember { mutableStateOf<String?>(settings.sabbathDay) }  // 휴무 요일
    // 휴무일 추가 값
    var newCustomDate by remember { mutableStateOf("") } // 휴무일 날짜
    var newCustomName by remember { mutableStateOf("") } // 휴무일 이름
    var showDatePicker by remember { mutableStateOf(false) } // DatePicker 표시 여부

    // 스크롤 상태
    val mainScrollState = rememberScrollState() // 메인
    val publicHolidaysScrollState = rememberScrollState() // 공휴일 목록
    val customDaysScrollState = rememberScrollState() // 휴무일 목록

    // 설정 값이 변경될 경우 상태 자동 동기화 (자동 갱신)
    LaunchedEffect(settings) {
        publicHolidays = settings.publicHolidays
        customDays = settings.customDays
        sabbathDay = settings.sabbathDay
    }

    // UI
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "휴일 설정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 본문
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(mainScrollState)
                ) {
                    // 공휴일

                    Text(
                        text = "공휴일 (자동)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BasicTertiary
                    )

                    Text(
                        text = "시스템 연도에 맞춰 자동으로 불러옵니다",
                        fontSize = 12.sp,
                        color = TextGray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 공휴일 목록

                    // 공휴일이 없을 때
                    if (publicHolidays.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoadingHolidays) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = BasicTertiary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "불러온 공휴일이 없습니다",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                        }
                    } else {
                        // 공휴일 개수 표시
                        Text(
                            text = "등록된 공휴일 수 :  총 ${publicHolidays.size}개",
                            fontSize = 12.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        // 공휴일 리스트
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BackgroundWhite
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(publicHolidaysScrollState)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                publicHolidays
                                    .filter { holiday -> holiday.date.isNotBlank() }
                                    .forEach { holiday ->
                                        val displayDate = DateCalculator.formatDisplayDate(holiday.date)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = White,
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = holiday.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BasicTertiary
                                                )
                                                Text(
                                                    text = displayDate,
                                                    fontSize = 12.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 휴무일
                    Text(
                        text = "휴무일 (수동 추가)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BasicSecondary
                    )

                    Text(
                        text = "특정 날짜를 수동으로 추가하여 제외할 수 있습니다",
                        fontSize = 12.sp,
                        color = TextGray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // 휴무일 추가
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCustomDate,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true },
                            label = { Text("날짜", fontSize = 14.sp) },
                            placeholder = { Text("yyyy-MM-dd", fontSize = 14.sp) },
                            enabled = false,
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Black,
                                disabledBorderColor = Gray
                            )
                        )
                        if (showDatePicker) {
                            val datePickerState = rememberDatePickerState()
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val cal = Calendar.getInstance().apply {
                                                timeInMillis = millis
                                            }
                                            newCustomDate = String.format(
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

                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newCustomName,
                                onValueChange = { newCustomName = it },
                                modifier = Modifier.weight(1f)
                                    .fillMaxHeight(),
                                label = { Text("내용", fontSize = 14.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                            )

                            Button(
                                onClick = {
                                    if (newCustomDate.isNotBlank()) {
                                        val newDay = Holiday(
                                            date = newCustomDate,
                                            name = newCustomName.ifBlank { "휴무일" }
                                        )
                                        onAddCustomDay(newDay)
                                        customDays = customDays + newDay
                                        newCustomDate = ""
                                        newCustomName = ""
                                    }
                                },
                                modifier = Modifier.fillMaxHeight(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BasicSecondary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                Text("추가", fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 휴무일 목록
                    if (customDays.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "등록된 휴무일이 없습니다",
                                fontSize = 14.sp,
                                color = TextGray
                            )
                        }
                    } else {
                        // 휴무일 개수 표시
                        Text(
                            text = "등록된 휴무일 수 : 총 ${customDays.size}개",
                            fontSize = 12.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BackgroundRed
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(customDaysScrollState)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                customDays
                                    .filter { holiday -> holiday.date.isNotBlank() }
                                    .forEach { holiday ->
                                        val displayDate =
                                            DateCalculator.formatDisplayDate(holiday.date)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = White,
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = holiday.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BasicSecondary
                                                )
                                                Text(
                                                    text = displayDate,
                                                    fontSize = 12.sp,
                                                    color = TextSecondary
                                                )
                                            }

                                            TextButton(
                                                onClick = {
                                                    onRemoveCustomDay(holiday.date)
                                                    customDays =
                                                        customDays.filter { it.date != holiday.date }
                                                }
                                            ) {
                                                Text(
                                                    "삭제",
                                                    color = BasicSecondary,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 저장 버튼
                Button(
                    onClick = {
                        onSave(
                            Settings(
                                publicHolidays = publicHolidays,
                                customDays = customDays,
                                sabbathDay = sabbathDay
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BasicPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "저장",
                        color = White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}