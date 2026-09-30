package com.example.ddayapp.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// 데이터 저장, 어플 내부에 저장하므로 어플 삭제 시 데이터가 사라짐

class PrefsHelper(context: Context) {

    private val prefs: SharedPreferences = // SharedPreferences 인스턴스 : 데이터 저장
        context.getSharedPreferences("dday_prefs", Context.MODE_PRIVATE) // 데이터 저장 파일 명 : dday_prefs, MODE_PRIVATE : 이 앱에서만 해당 파일에 접근 가능하도록 설정해주는 명령어

    private val gson = Gson() // Gson 라이브러리 인스턴스 : 객체와 json 문자열 간의 변환을 처리

    companion object { // 데이터를 저장할 때 사용할 키 값을 상수로 정의, companion object 안에 선언하여 클래스 이름을 통해 바로 접근 가능
        private const val KEY_DDAYS = "ddays" // 디데이 목록을 저장하기 위한 키
        private const val KEY_SETTINGS = "settings" // 설정 정보를 저장하기 위한 키
    }

    // 디데이 목록 저장

    fun saveDDays(ddays: List<DDay>) { 
        val json = gson.toJson(ddays) // 디데이 객체 리스트를 JSON 형태의 문자열로 변환
        prefs.edit().putString(KEY_DDAYS, json).apply() // SharedPreferences에 (KEY_DDAYS, json)쌍으로 저장하고, apply를 호출하여 비동기적으로 반영
    }

    // 디데이 목록 로드

    fun loadDDays(): List<DDay> {
        val json = prefs.getString(KEY_DDAYS, null) ?: return emptyList()
        // KEY_DDAYS 키를 사용하여 저장된 JSON 문자열 불러오기, 데이터가 없으면 null을 불러옴
        // ?: return emptyList() : null인 경우 즉시 빈 리스트 반환
        val type = object : TypeToken<List<DDay>>() {}.type // Gson이 List<DDay> 형태의 제네릭 타입을 인식할 수 있도록 TypeToken을 사용해 타입을 명시
        val loadedList:  List<DDay> = gson.fromJson(json, type) ?: emptyList() // JSON 문자열을 List<DDay> 객체로 다시 변환(역직렬화), 변환 실패 시 빈 리스트 반환

        // 오름차순 정렬 : order 속성 기준
        return loadedList.sortedBy { it.order }
    }

    // 설정 저장

    fun saveSettings(settings: Settings) {
        val json = gson.toJson(settings) // Settings 객체를 JSON 문자열로 변환
        prefs.edit().putString(KEY_SETTINGS, json).apply() // SharedPreferences에 (KEY_SETTINGS, JSON 문자열)쌍으로 저장
    }

    // 설정 로드

    fun loadSettings(): Settings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return Settings()
        // KEY_SETTINGS 키로 저장된 JSON 문자열을 불러옴, 데이터가 없으면 null 반환
        // ?: return Settings() : null인 경우 기본 Settings 객체를 반환
        return gson.fromJson(json, Settings::class.java) ?: Settings() // JSON 문자열을 다시 Settings 클래스 객체로 변환하여 반환, 변환 실패 시 기본 Settings 객체를 반환
    }
}