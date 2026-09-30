package com.example.ddayapp.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ddayapp.ui.theme.DdayAppTheme

// 1X1 투명 D-Day 위젯 설정 Activity
// 역할 : 위젯 생성 시 어떤 D-day를 표시할 지 선택, 선택 결과를 저장, 위젯 업데이트 후 종료
class DdayStyle1TransparentWidgetConfigActivity : ComponentActivity() {

    // 현재 위젯의 고유 ID
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    // 재설정 여부 (이미 존재하는 위젯을 수정하는 경우)
    private var isReconfiguring = false
    
    companion object {
        private const val TAG = "Style1TransparentConfig"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 기본 결과는 취소 (사용자가 완료하지 않으면 위젯 생성 안 됨)
        setResult(Activity.RESULT_CANCELED)

        // 시스템에서 전달된 위젯 ID 가져오기
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // ID가 없으면 종료
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // 재설정 모드 여부 확인 (이미 위젯이 홈화면에 있는 경우)
        isReconfiguring = intent?.flags?.and(Intent.FLAG_ACTIVITY_NEW_TASK) != 0

        // Compose UI 설정 화면 표시
        setContent {
            DdayAppTheme {
                WidgetConfigScreen(
                    title = "1x1 미니 위젯 투명",
                    // D-Day 선택 시
                    onDdaySelected = { ddayId ->
                        saveWidgetConfig(ddayId)
                        finishWithSuccess()
                    },
                    // 취소 시 종료
                    onCancel = { finish() }
                )
            }
        }
    }

    // 위젯 설정 저장
    // @param ddayId 선택된 D-Day ID
    private fun saveWidgetConfig(ddayId: Long) {
        val prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE)
        prefs.edit()
            .putLong("widget_style1_transparent_${appWidgetId}_dday_id", ddayId)
            .apply()
        Log.d(TAG, "Saved: widget_style1_transparent_${appWidgetId}_dday_id = $ddayId")
    }

    // 설정 완료 후 위젯 업데이트 및 결과 반환
    private fun finishWithSuccess() {
        // 위젯 즉시 업데이트
        val appWidgetManager = AppWidgetManager.getInstance(this)
        DdayStyle1TransparentWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)

        // 시스템에 성공 결과 전달
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
