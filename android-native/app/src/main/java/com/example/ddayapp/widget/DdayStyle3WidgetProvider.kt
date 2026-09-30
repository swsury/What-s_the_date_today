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
import android.graphics.Color
import android.util.Log

class DdayStyle3WidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "Style3Widget"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                val prefsHelper = PrefsHelper(context)
                val widgetPrefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)

                val ddayId = widgetPrefs.getLong("widget_style3_${appWidgetId}_dday_id", -1L)
                val views = RemoteViews(context.packageName, R.layout.widget_dday_style3)

                if (ddayId != -1L) {
                    val ddays = prefsHelper.loadDDays()
                    val dday = ddays.find { it.id == ddayId }
                    val settings = prefsHelper.loadSettings()

                    if (dday != null) {
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

                        views.setTextViewText(R.id.widget_label, dday.labelTitle)
                        views.setTextViewText(R.id.widget_title, dday.title)
                        views.setTextViewText(R.id.widget_dday, ddayText)
                        views.setTextViewText(R.id.widget_date, dday.date)

                        // 🔥 배경색 설정
                        try {
                            val color = Color.parseColor(dday.color)
                            views.setInt(R.id.widget_background, "setBackgroundColor", color)
                        } catch (e: Exception) {
                            views.setInt(R.id.widget_background, "setBackgroundColor", Color.parseColor("#468BD7"))
                        }

                        // 🔥 위젯 클릭 시 해당 D-day 편집 화면 열기
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("dday_id", dday.id)
                            putExtra("open_edit", true)
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId, // 고유한 requestCode 사용
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                    } else {
                        // D-day가 삭제된 경우
                        setDefaultContent(views)
                        setDefaultClickIntent(context, views, appWidgetId)
                    }
                } else {
                    // 위젯 설정이 안 된 경우
                    setDefaultContent(views)
                    setDefaultClickIntent(context, views, appWidgetId)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget $appWidgetId", e)
            }
        }

        private fun setDefaultContent(views: RemoteViews) {
            views.setTextViewText(R.id.widget_label, "D-day")
            views.setTextViewText(R.id.widget_title, "위젯 설정")
            views.setTextViewText(R.id.widget_dday, "")
            views.setTextViewText(R.id.widget_date, "터치하여 설정")

            // 기본 배경색
            views.setInt(R.id.widget_background, "setBackgroundColor", Color.parseColor("#468BD7"))
        }

        // 🔥 기본 클릭 시 메인 화면만 열기
        private fun setDefaultClickIntent(context: Context, views: RemoteViews, appWidgetId: Int) {
            val intent = Intent(context, MainActivity:: class.java).apply {
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

        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, DdayStyle3WidgetProvider::class.java)
                )
                appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update all widgets", e)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val widgetPrefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val editor = widgetPrefs.edit()
        appWidgetIds.forEach { editor.remove("widget_style3_${it}_dday_id") }
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