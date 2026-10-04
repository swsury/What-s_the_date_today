package com.example.ddayapp.widget

// Compose UI 구성 요소 및 레이아웃 관련 import
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
// UI 스타일 관련 import
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
// 상태 관리 및 기본 Compose 기능
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 데이터 및 유틸 클래스
import com.example.ddayapp.data.PrefsHelper
import com.example.ddayapp.ui.theme.toComposeColor
import com.example.ddayapp.utils.DateCalculator
import com.example.ddayapp.ui.theme.*
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.toArgb

// Material3의 실험적 API 사용 허용
@OptIn(ExperimentalMaterial3Api::class)
// 위젯 설정 화면 Composable
// 역할 : 저장된 D-day 목록을 불러와 리스트 형태로 표시, 사용자가 특정 D-day를 선택하면 ID를 콜백으로 전달, 취소 버튼 클릭 시 설정 화면 종료
/*
 * @param title 상단 AppBar에 표시할 제목
 * @param onDdaySelected 선택된 D-day의 ID를 전달하는 콜백
 * @param onCancel 취소 버튼 클릭 시 실행되는 콜백
 */
@Composable
fun WidgetConfigScreen(
    title: String,
    onDdaySelected:  (Long) -> Unit,
    onCancel: () -> Unit
) {
    // 현재 Context 가져오기 (SharedPreferences 접근 등에 필요)
    val context = LocalContext.current
    // PrefsHelper를 remember로 생성하여 recomposition 시 재생성 방지
    val prefsHelper = remember { PrefsHelper(context) }
    // 저장된 D-day 목록 불러오기
    val ddays = remember { prefsHelper.loadDDays() }
    // 설정값 (공휴일, 사용자 지정 제외일 등) 불러오기
    val settings = remember { prefsHelper.loadSettings() }

    // 공휴일 날짜들을 Set으로 변환 (빠른 검색을 위해)
    val publicHolidays = remember {
        settings.publicHolidays.map { it.date }.toSet()
    }
    // 사용자 정의 제외일을 Set으로 변환
    val customDays = remember {
        settings.customDays.map { it.date }.toSet()
    }

    // 전체 화면 구조 : TopAppBar + Body(리스트 또는 빈 상태 UI)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 상단 제목
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                },
                navigationIcon = {
                    // 좌측 닫기 버튼 (취소)
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "취소",
                            tint = White
                        )
                    }
                },
                // AppBar 배경 색상 설정
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BasicPrimary
                )
            )
        }
    ) { paddingValues ->
        // D-day가 하나도 없는 경우 → 안내 UI 표시
        if (ddays.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 아이콘 (이모지)
                Text(text = "📅", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                // 메인 안내 문구
                Text(
                    text = "등록된 D-day가 없습니다",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 서브 안내 문구
                Text(
                    text = "앱에서 D-day를 먼저 추가해주세요",
                    fontSize = 14.sp,
                    color = Gray
                )
            }
        } else {
            // D-day 목록이 있는 경우 → 리스트 표시
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                // 리스트 내부 패딩
                contentPadding = PaddingValues(16.dp),
                // 각 아이템 간 간격
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // D-day 리스트 반복
                items(ddays) { dday ->
                    // D-day 계산 : 공휴일, 사용자 제외일, 제외 요일 등을 반영, 오류 발생시 기본 값 D-day 표시
                    val ddayText = try {
                        DateCalculator.calculateDDay(
                            targetDate = dday.date,
                            excludePublicHolidays = dday.excludePublicHolidays,
                            excludeCustomDays = dday.excludeCustomDays,
                            excludedWeekdays = dday.excludedWeekdays,
                            publicHolidays = publicHolidays,
                            customDays = customDays
                        )
                    } catch (e: Exception) {
                        "D-Day"
                    }

                    // 개별 D-day 카드 UI
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            // 카드 클릭 시 해당 D-day ID 전달
                            .clickable { onDdaySelected(dday.id) },
                        shape = RoundedCornerShape(12.dp),
                        // 카드 그림자
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 카드 상단 컬러 바 (라벨 영역)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .background(
                                        dday.color.toComposeColor(), // 색상 변환
                                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = dday.labelTitle, // 라벨 제목
                                    color = White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // 카드 본문 영역
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(White)
                                    .padding(16.dp)
                            ) {
                                // D-day 제목
                                Text(
                                    text = dday.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                // 하단 정보 영역 : 왼쪽-D-day 결과, 오른쪽-날짜
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // 계산된 D-day 값
                                    Text(
                                        text = ddayText,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = dday.color.toComposeColor()
                                    )
                                    // 기준 날짜
                                    Text(
                                        text = dday.date,
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}