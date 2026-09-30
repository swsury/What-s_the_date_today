package com.example.ddayapp.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ddayapp.ui.theme.DdayAppTheme

class DdayStyle3WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    
    companion object {
        private const val TAG = "Style3WidgetConfig"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            DdayAppTheme {
                WidgetConfigScreen(
                    title = "2x2 미니멀 위젯",
                    onDdaySelected = { ddayId ->
                        saveWidgetConfig(ddayId)
                        finishWithSuccess()
                    },
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun saveWidgetConfig(ddayId: Long) {
        val prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE)
        prefs.edit()
            .putLong("widget_style3_${appWidgetId}_dday_id", ddayId)
            .apply()
        Log.d(TAG, "Saved: widget_style3_${appWidgetId}_dday_id = $ddayId")
    }

    private fun finishWithSuccess() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        DdayStyle3WidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}