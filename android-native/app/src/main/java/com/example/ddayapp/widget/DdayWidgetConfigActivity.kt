package com.example.ddayapp.widget

// Android 기본 Activity 및 위젯 관련 클래스
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
// Compose 기반 Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// Compose UI 관련
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// Material UI
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
// etc
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 앱 내부 클래스
import com.example.ddayapp.data.PrefsHelper
import com.example.ddayapp.ui.theme.DdayAppTheme
import com.example.ddayapp.ui.theme.toComposeColor
import com.example.ddayapp.utils.DateCalculator

// 위젯 추가 시 실행되는 설정 화면 Activity
// 역할 : 사용자가 홈 화면에 위젯을 추가할 때 실행됨, 어떤 D-day를 위젯에 표시할지 선택하게 함
class DdayWidgetConfigActivity : ComponentActivity() {

    // 현재 설정 중인 위젯 ID (시스템이 전달해줌)
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    companion object {
        private const val TAG = "WidgetConfig"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called")

        // 결과를 취소로 설정 (사용자가 설정을 완료하지 않으면 위젯 추가 취소)
        setResult(Activity.RESULT_CANCELED)

        // 위젯 ID 가져오기 : 각 위젯을 구분하는 고유 값
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        Log.d(TAG, "Widget ID: $appWidgetId")

        // 위젯 ID가 유효하지 않으면 Activity 종료
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "Invalid widget ID, finishing")
            finish()
            return
        }

        // Compose UI 설정
        setContent {
            DdayAppTheme {
                // 실제 UI 화면 (D-day 선택 리스트)
                WidgetConfigScreen(
                    // 사용자가 D-day를 선택했을 때 실행
                    onDdaySelected = { ddayId ->
                        Log.d(TAG, "D-day selected: $ddayId")
                        // 선택한 D-day를 위젯 설정으로 저장
                        saveWidgetConfig(ddayId)
                        // 위젯 업데이트 + 성공 종료
                        finishWithSuccess()
                    },
                    // 취소 버튼 클릭 시
                    onCancel = {
                        Log.d(TAG, "Configuration cancelled")
                        finish()
                    }
                )
            }
        }
    }

    // 선택한 D-day를 SharedPreferences에 저장
    // key 형태 : widget_{위젯ID}_dday_id
    // 이유 : 위젯은 여러 개 생성 가능, 각 위젯마다 다른 D-day를 보여줄 수 있어야 함
    private fun saveWidgetConfig(ddayId: Long) {
        val prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE)
        prefs.edit()
            .putLong("widget_${appWidgetId}_dday_id", ddayId)
            .apply()
        Log.d(TAG, "Saved widget config:  widget_${appWidgetId}_dday_id = $ddayId")
    }

    // 설정 완료 후 Activity 종료 처리
    // 위젯 UI 갱신, RESULT_OK 반환 (이걸 해야 위젯 생성 성공), Activity 종료
    private fun finishWithSuccess() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        // 실제 위젯 UI 업데이트
        DdayWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)

        // 결과 OK 설정 (필수)
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        Log.d(TAG, "Finishing with success")
        finish()
    }
}

// 위젯 설정 화면 UI (Compose)
// 역할 : 저장된 D-day 목록을 보여줌, 하나 선택하면 Activity로 콜백 전달
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigScreen(
    onDdaySelected: (Long) -> Unit, // 선택 시 호출
    onCancel: () -> Unit // 취소 시 호출
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // SharedPreferences 헬퍼 객체 (D-day 데이터 및 설정 로드)
    val prefsHelper = remember { PrefsHelper(context) }
    // 저장된 D-day 목록
    val ddays = remember { prefsHelper.loadDDays() }
    // 사용자 설정 (공휴일, 커스텀 날짜 등)
    val settings = remember { prefsHelper.loadSettings() }

    // 공휴일 날짜 Set : 빠른 검색을 위해 Set으로 변환
    val publicHolidays = remember {
        settings.publicHolidays.map { it.date }.toSet()
    }

    // 사용자 정의 제외 날짜 Set
    val customDays = remember {
        settings.customDays.map { it.date }.toSet()
    }

    // 기본 화면 구조 (TopBar + Content)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "위젯에 표시할 D-day 선택",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                // 왼쪽 닫기 버튼
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "취소",
                            tint = Color.White
                        )
                    }
                },
                // 앱바 배경 색상
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFAACBE0)
                )
            )
        }
    ) { paddingValues ->
        // D-day가 하나도 없는 경우
        if (ddays.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 아이콘
                Text(
                    text = "📅",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // 안내 메시지
                Text(
                    text = "등록된 D-day가 없습니다",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "앱에서 D-day를 먼저 추가해주세요",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            // D-day 목록 리스트
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ddays) { dday ->
                    // D-day 계산 (공휴일, 특정 요일 제외 옵션 반영)
                    val ddayText = try {
                        DateCalculator.calculateDDay(
                            targetDate = dday.date,
                            excludePublicHolidays = dday.excludePublicHolidays,
                            excludeCustomDays = dday.excludeCustomDays,
                            excludedWeekdays = dday.excludedWeekdays,
                            publicHolidays = publicHolidays,
                            customDays = customDays
                        )
                    } catch (e:  Exception) {
                        // 계산 실패 시 기본값
                        "D-Day"
                    }

                    // D-day 카드 UI : 클릭 시 해당 D-day 선택
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDdaySelected(dday.id) },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 카드 상단 컬러 라벨 영역
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .background(
                                        dday.color.toComposeColor(),
                                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = dday.labelTitle,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // 내용 : 카드 본문 영역
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                // D-day 제목
                                Text(
                                    text = dday.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B1C1F)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // D-day 값 + 날짜
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // D-3, D+5 등 표시
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
                                        color = Color(0xFF666666)
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