package com.example.ddayapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ddayapp.data.DDay
import com.example.ddayapp.data.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// 앱의 로컬 데이터(디데이 목록, 설정)를 SharedPreferences에 JSON 형태로 저장/불러오는 헬퍼 클래스
class PreferencesHelper(context: Context) {

    // SharedPreferences 인스턴스 생성
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("dday_prefs", Context.MODE_PRIVATE)
    
    // 객체 <-> JSON 변환용 Gson
    private val gson = Gson()
    
    companion object {
        // SharedPreferences 내부 키 값
        private const val KEY_DDAYS = "ddays"
        private const val KEY_SETTINGS = "settings"
    }
    

    //D-day 리스트 저장
    // @param ddays 저장할 DDay 목록

    fun saveDDays(ddays: List<DDay>) {
        val json = gson.toJson(ddays)
        prefs.edit().putString(KEY_DDAYS, json).apply()
    }
    
    // 저장된 D-day 리스트 로드
    // @return 저장된 DDay 목록 (없으면 빈 리스트)

    fun loadDDays(): List<DDay> {
        val json = prefs.getString(KEY_DDAYS, null) ?: return emptyList()
        val type = object : TypeToken<List<DDay>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    

    // 설정 데이터 저장
    // @param settings 저장할 Settings 객체

    fun saveSettings(settings: Settings) {
        val json = gson.toJson(settings)
        prefs.edit().putString(KEY_SETTINGS, json).apply()
    }

    // 설정 데이터 로드
    // @return 저장된 Settings 객체 (없으면 기본 Settings 반환)

    fun loadSettings(): Settings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return Settings()
        return try {
            gson.fromJson(json, Settings::class.java) ?: Settings()
        } catch (e: Exception) {
            Settings()
        }
    }

    // 모든 저장 데이터 삭제

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
