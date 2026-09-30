package com.example.ddayapp.widget

// 위젯 클릭 이벤트 처리용
import android.app.PendingIntent
// 위젯 관리 클래스
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
// 위젯 UI 구성 클래스 (XML 기반)
import android.widget.RemoteViews
import com.example.ddayapp.MainActivity
import com.example.ddayapp.R
import com.example.ddayapp.data.PrefsHelper
import com.example.ddayapp.utils.DateCalculator
import android.graphics.Color
import android.util.Log

class DdayWidgetProvider :  AppWidgetProvider() {

    companion object {
        private const val TAG = "DdayWidgetProvider"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId:  Int
        ) {
            try {
                val prefsHelper = PrefsHelper(context)
                val widgetPrefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)

                // 위젯에 설정된 D-day ID와 스타일 가져오기
                val ddayId = widgetPrefs.getLong("widget_${appWidgetId}_dday_id", -1L)
                val style = widgetPrefs.getInt("widget_${appWidgetId}_style", 1)

                // 스타일에 따른 레이아웃 선택
                val layoutId = when (style) {
                    1 -> R.layout.widget_dday_style1
                    2 -> R.layout.widget_dday_style2
                    3 -> R.layout.widget_dday_style3
                    else -> R.layout.widget_dday_style1
                }

                val views = RemoteViews(context.packageName, layoutId)

                if (ddayId != -1L) {
                    // D-day 데이터 로드
                    val ddays = prefsHelper.loadDDays()
                    val dday = ddays.find { it.id == ddayId }
                    val settings = prefsHelper.loadSettings()

                    if (dday != null) {
                        // D-day 정보 표시
                        views.setTextViewText(R.id.widget_label, dday.labelTitle)
                        views.setTextViewText(R.id.widget_title, dday.title)

                        // D-day 계산
                        val publicHolidays = settings.publicHolidays.map { it.date }.toSet()
                        val customDays = settings.customDays.map { it.date }.toSet()

                        val ddayText = DateCalculator.calculateDDay(
                            targetDate = dday.date,
                            excludePublicHolidays = dday.excludePublicHolidays,
                            excludeCustomDays = dday.excludeCustomDays,
                            excludedWeekdays = dday.excludedWeekdays,
                            publicHolidays = publicHolidays,
                            customDays = customDays
                        )

                        views.setTextViewText(R.id.widget_dday, ddayText)
                        views.setTextViewText(R.id.widget_date, dday.date)

                        // 배경 색상 설정
                        try {
                            val color = Color.parseColor(dday.color)
                            views.setInt(R.id.widget_background, "setBackgroundColor", color)
                        } catch (e:  Exception) {
                            Log.e(TAG, "Failed to parse color: ${dday.color}", e)
                            views.setInt(R.id.widget_background, "setBackgroundColor", Color.parseColor("#468BD7"))
                        }
                    } else {
                        // D-day가 삭제된 경우
                        setDefaultWidgetContent(views, "D-day가 삭제되었습니다", "위젯을 재설정해주세요", style)
                    }
                } else {
                    // 위젯 설정이 안된 경우
                    setDefaultWidgetContent(views, "D-day 위젯", "위젯을 설정해주세요", style)
                }

                // 클릭 시 앱 열기
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                // 위젯 업데이트
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget $appWidgetId", e)
            }
        }

        private fun setDefaultWidgetContent(views: RemoteViews, title: String, subtitle: String, style: Int) {
            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_dday, "")
            views.setInt(R.id.widget_background, "setBackgroundColor", Color.parseColor("#468BD7"))

            // 스타일 4는 라벨과 날짜가 숨겨져 있음
            if (style != 4) {
                views.setTextViewText(R.id.widget_label, "D-day")
                views.setTextViewText(R.id.widget_date, subtitle)
            }
        }

        /**
         * 모든 위젯 업데이트
         */
        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, DdayWidgetProvider::class.java)
                )
                appWidgetIds.forEach { appWidgetId ->
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update all widgets", e)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds:  IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // 위젯 삭제 시 설정 정보도 삭제
        val widgetPrefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val editor = widgetPrefs.edit()
        appWidgetIds.forEach { appWidgetId ->
            editor.remove("widget_${appWidgetId}_dday_id")
            editor.remove("widget_${appWidgetId}_style")
        }
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