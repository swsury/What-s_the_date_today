package com.example.ddayapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.ddayapp.MainActivity
import com.example.ddayapp.R
import com.example.ddayapp.data.PrefsHelper
import com.example.ddayapp.utils.DateCalculator
import android.util.Log
import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ddayapp.ui.theme.*
import android.graphics.Color as AndroidColor


// 3X1 가로형 투명 D-Day 위젯 설정 Provider
// 역할 : 위젯 UI 업데이트, D-Day 계산 및 표시, 클릭 이벤트 처리
class DdayStyle2TransparentWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "Style2TransparentWidget"

        // 개별 위젯 업데이트
        // @param appWidgetId 엡데이트할 위젯 ID
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                val prefsHelper = PrefsHelper(context)

                // 위젯별 설정 저장소
                val widgetPrefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)

                // 이 위젯에 연결된 D-Day ID 가져오기
                val ddayId = widgetPrefs.getLong("widget_style2_transparent_${appWidgetId}_dday_id", -1L)

                // 위젯 UI 객체 생성
                val views = RemoteViews(context.packageName, R.layout.widget_dday_style2_transparent)

                if (ddayId != -1L) {
                    // 저장된 데이터 로드
                    val ddays = prefsHelper.loadDDays()
                    val dday = ddays.find { it.id == ddayId }
                    val settings = prefsHelper.loadSettings()

                    if (dday != null) {
                        // 공휴일 / 사용자 지정 날짜 Set 변환
                        val publicHolidays = settings.publicHolidays.map { it.date }.toSet()
                        val customDays = settings.customDays.map { it.date }.toSet()

                        // D-Day 계산
                        val ddayText = DateCalculator.calculateDDay(
                            targetDate = dday.date,
                            excludePublicHolidays = dday.excludePublicHolidays,
                            excludeCustomDays = dday.excludeCustomDays,
                            excludedWeekdays = dday.excludedWeekdays,
                            publicHolidays = publicHolidays,
                            customDays = customDays
                        )

                        // UI 데이터 바인딩
                        views.setTextViewText(R.id.widget_label, dday.labelTitle)
                        views.setTextViewText(R.id.widget_title, dday.title)
                        views.setTextViewText(R.id.widget_dday, ddayText)
                        views.setTextViewText(R.id.widget_date, dday.date)

                        // 텍스트 색상 적용 : D-day Card 색상
                        try {
                            val color = Color.parseColor(dday.color)
                            views.setTextColor(R.id.widget_label, color)  // 라벨 색상
                            views.setTextColor(R.id.widget_dday, color)   // D-day 숫자 색상
                            views.setTextColor(R.id.widget_title, color)  // 제목 색상 (선택사항)
                            views.setTextColor(R.id.widget_date, color) //날짜 색상
                        } catch (e: Exception) {
                            // 색상 파싱 실패 시 기본 색상 유지
                            views.setTextColor(R.id.widget_label, BasicPrimary.toArgb())
                            views.setTextColor(R.id.widget_dday, BasicPrimary.toArgb())
                        }

                        // 위젯 클릭 시 해당 D-day 편집 화면 열기
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("dday_id", dday.id)
                            putExtra("open_edit", true)
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId, // 위젯별 고유 requestCode
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                    } else {
                        // D-Day 데이터가 없을 경우 기본 UI
                        setDefaultContent(views)
                        setDefaultClickIntent(context, views, appWidgetId)
                    }
                } else {
                    // 위젯 설정이 안 된 경우
                    setDefaultContent(views)
                    setDefaultClickIntent(context, views, appWidgetId)
                }

                // 위젯 업데이트 반영
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget $appWidgetId", e)
            }
        }

        // 기본 UI 설정 (초기 상태)
        private fun setDefaultContent(views: RemoteViews) {
            views.setTextViewText(R.id.widget_label, "D-day")
            views.setTextViewText(R.id.widget_title, "위젯 설정")
            views.setTextViewText(R.id.widget_dday, "")
            views.setTextViewText(R.id.widget_date, "터치하여 설정")

            // 기본 텍스트 색상
            views.setTextColor(R.id.widget_label, BasicPrimary.toArgb())
            views.setTextColor(R.id.widget_dday, BasicPrimary.toArgb())
            views.setTextColor(R.id.widget_title, BasicPrimary.toArgb())
            views.setTextColor(R.id.widget_date, BasicPrimary.toArgb())
        }

        // 기본 클릭 이벤트 (앱 메인 화면 이동)
        private fun setDefaultClickIntent(context: Context, views: RemoteViews, appWidgetId: Int) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId, // 고유한 requestCode
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        }

        // 모든 위젯 갱신
        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, DdayStyle2TransparentWidgetProvider::class.java)
                )
                appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update all widgets", e)
            }
        }
    }

    // 시스템이 위젯 업데이트 요청 시 호출
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
    }

    // 위젯 삭제 시 호출 (데이터 정리)
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val widgetPrefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val editor = widgetPrefs.edit()
        appWidgetIds.forEach { editor.remove("widget_style2_transparent_${it}_dday_id") }
        editor.apply()
    }

    override fun onEnabled(context:  Context) {
        super.onEnabled(context)
        Log.d(TAG, "Widget enabled")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d(TAG, "Widget disabled")
    }
}
