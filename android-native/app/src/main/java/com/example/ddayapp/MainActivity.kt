package com.example.ddayapp

// Android 기본 Activity 및 생명주기 관련 클래스
import android.os.Bundle
// Compose를 사용하는 Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// Compose UI 구성 요소
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// Compose UI 구성 요소
import com.example.ddayapp.ui.DdayScreen
// 앱 테마
import com.example.ddayapp.ui.theme.DdayAppTheme

// 앱의 메인 진입 Activity
// 역할 : 앱 실행 시 최초 화면(DdayScreen) 구성, 위젯에서 전달된 Intent 데이터를 받아 특정 D-day 편집 화면으로 진입 처리
class MainActivity : ComponentActivity() {
    // Activity 최초 생성 시 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        * 위젯 또는 외부에서 전달된 Intent 데이터 처리
        - dday_id: 선택된 D-day의 ID
        - open_edit: 해당 D-day를 바로 "편집 모드"로 열지 여부
        * 기본값
        - ID → -1L (유효하지 않은 값)
        - open_edit → false
         */

        val ddayIdFromWidget = intent?.getLongExtra("dday_id", -1L) ?: -1L
        val shouldOpenEdit = intent?.getBooleanExtra("open_edit", false) ?: false

        // Compose UI 시작 지점
        setContent {
            // 앱 전체 테마 적용
            DdayAppTheme {
                // Surface : 화면의 기본 배경 및 레이아웃 컨테이너 역할
                Surface(
                    // 화면 전체 사용
                    modifier = Modifier.fillMaxSize(),
                    // 테마 배경색 적용
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*
                     * 메인 화면 Composable

                     * initialDdayIdToEdit:
                     - 특정 조건일 때만 전달 → open_edit == true AND 유효한 ID일 때
                     - 조건이 아니면 null (일반 진입)

                     * 결과:
                     - 값이 있으면 → 해당 D-day 편집 화면으로 바로 이동
                     - null이면 → 기본 리스트 화면 표시
                     */
                    DdayScreen(
                        initialDdayIdToEdit = if (shouldOpenEdit && ddayIdFromWidget != -1L) {
                            ddayIdFromWidget
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }

    /*
    * Activity가 이미 실행 중일 때 새로운 Intent가 들어오는 경우 호출
    * 상황 예시 : 앱이 이미 실행 중일 때 새 Intent 처리, 사용자가 위젯을 클릭했는데 앱이 이미 켜져 있는 상태
    * 처리 방식 : 1. 새로운 Intent를 현재 Activity에 반영 (setIntent), 2. recreate() 호출 → onCreate 다시 실행 → 최신 Intent 값을 기반으로 UI 다시 구성
     */
    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        // 새 Intent로 액티비티 재시작
        recreate()
    }
}