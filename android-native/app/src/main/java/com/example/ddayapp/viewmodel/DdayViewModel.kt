package com.example.ddayapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ddayapp.utils.HolidayApi
import com.example.ddayapp.data.*
import com.example.ddayapp.widget.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

// D-Day 앱의 핵심 ViewModel
// 역할 : D-day 목록 관리, 설정 관리, 공휴일 API 연동, 위젯 동기화
class DdayViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsHelper = PrefsHelper(application)
    private val context = application

    private val _ddays = MutableStateFlow<List<DDay>>(emptyList())
    val ddays: StateFlow<List<DDay>> = _ddays

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings

    private val _isLoadingHolidays = MutableStateFlow(false)
    val isLoadingHolidays: StateFlow<Boolean> = _isLoadingHolidays

    init {
        loadData()
        autoLoadCurrentYearHolidays()
    }

    // 초기 데이터 로드
    private fun loadData() {
        _ddays.value = prefsHelper.loadDDays()
        _settings.value = prefsHelper.loadSettings()
        checkAndUpdateYearHolidays()
    }

    //위젯 자동 업데이트
    private fun updateAllWidgets() {
        DdayStyle1WidgetProvider.updateAllWidgets(context)
        DdayStyle2WidgetProvider.updateAllWidgets(context)
        DdayStyle3WidgetProvider.updateAllWidgets(context)  // ✅ 추가!
        DdayStyle1TransparentWidgetProvider.updateAllWidgets(context)
        DdayStyle2TransparentWidgetProvider.updateAllWidgets(context)
        DdayStyle3TransparentWidgetProvider.updateAllWidgets(context)
    }

    // D-day 추가
    fun addDDay(dday: DDay) {
        val currentList = _ddays.value
        // 새 D-day는 맨 뒤에 추가되도록 order 설정
        val newOrder = (currentList.maxOfOrNull { it.order } ?: -1) + 1
        val newDDay = dday.copy(order = newOrder)

        _ddays.value = currentList + newDDay
        prefsHelper.saveDDays(_ddays.value)

        // 위젯 업데이트
        updateAllWidgets()
    }

    // D-day 수정
    fun updateDDay(dday: DDay) {
        _ddays.value = _ddays.value.map { item ->
            if (item.id == dday.id) dday else item
        }
        prefsHelper.saveDDays(_ddays.value)

        // 위젯 업데이트
        updateAllWidgets()
    }

    // D-day 삭제
    fun deleteDDay(id: Long) {
        _ddays.value = _ddays.value.filter { it.id != id }
        prefsHelper.saveDDays(_ddays.value)

        // 위젯 업데이트
        updateAllWidgets()
    }

    // D-day 순서 변경 (드래그 앤 드롭)
    fun reorderDDays(fromIndex: Int, toIndex: Int) {
        val currentList = _ddays.value.toMutableList()

        // 인덱스 유효성 검사
        if (fromIndex == toIndex) return
        if (fromIndex !in currentList.indices) return
        if (toIndex !in currentList.indices) return

        // 아이템 이동
        val item = currentList.removeAt(fromIndex)
        currentList.add(toIndex, item)

        // order 재설정
        val reorderedList = currentList.mapIndexed { index, ddayItem ->
            ddayItem.copy(order = index)
        }

        _ddays.value = reorderedList
        prefsHelper.saveDDays(_ddays.value)

        // 위젯 업데이트
        updateAllWidgets()
    }

    // D-day 그룹 이동
    fun moveDdayToGroup(ddayId: Long, targetLabelTitle: String) {
        _ddays.value = _ddays.value.map { dday ->
            if (dday.id == ddayId) {
                dday.copy(labelTitle = targetLabelTitle)
            } else {
                dday
            }
        }
        prefsHelper.saveDDays(_ddays.value)
        updateAllWidgets()
    }

    // D-day 설정 업데이트
    fun updateSettings(settings: Settings) {
        _settings.value = settings
        prefsHelper.saveSettings(settings)

        // 위젯 업데이트
        updateAllWidgets()
    }

    // 휴무일 (사용자 지정 휴식일) 추가
    fun addCustomDay(holiday: Holiday) {
        val currentSettings = _settings.value
        val updatedCustomDays = (currentSettings.customDays + holiday)
            .distinctBy { it.date }
            .sortedBy { it.date }

        val newSettings = currentSettings.copy(customDays = updatedCustomDays)
        updateSettings(newSettings)
    }

    // 휴무일 (사용자 지정 휴식일) 삭제
    fun removeCustomDay(date: String) {
        val currentSettings = _settings.value
        val updatedCustomDays = currentSettings.customDays.filter { it.date != date }

        val newSettings = currentSettings.copy(customDays = updatedCustomDays)
        updateSettings(newSettings)
    }

    // 공휴일 자동 로드 (현재 연도)
    private fun autoLoadCurrentYearHolidays() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val currentSettings = _settings.value

        val currentYearHolidays = currentSettings.publicHolidays.filter { holiday ->
            holiday.date.startsWith(currentYear)
        }

        if (currentYearHolidays.isEmpty()) {
            fetchPublicHolidaysFromApi(currentYear)
        }
    }

    // 공휴일 자동 업데이트 (시스템 연도 변경 시)
    private fun checkAndUpdateYearHolidays() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val currentSettings = _settings.value

        val currentYearHolidaysCount = currentSettings.publicHolidays.count { holiday ->
            holiday.date.startsWith(currentYear)
        }

        if (currentYearHolidaysCount == 0) {
            fetchPublicHolidaysFromApi(currentYear)
        }
    }

    // 공휴일 API에서 정보 가져오기
    fun fetchPublicHolidaysFromApi(year: String, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoadingHolidays.value = true

            val result = HolidayApi.fetchHolidays(year)

            result.onSuccess { newHolidays:  List<Holiday> ->
                val currentSettings = _settings.value

                val otherYearHolidays = currentSettings.publicHolidays.filter { holiday: Holiday ->
                    ! holiday.date.startsWith(year)
                }

                val mergedHolidays = (otherYearHolidays + newHolidays)
                    .distinctBy { holiday: Holiday -> holiday.date }
                    .sortedBy { holiday: Holiday -> holiday.date }

                val newSettings = currentSettings.copy(publicHolidays = mergedHolidays)
                updateSettings(newSettings)

                onComplete?.invoke(true, "${year}년 공휴일 ${newHolidays.size}개를 추가했습니다.")
            }

            result.onFailure { exception:  Throwable ->
                onComplete?.invoke(false, "공휴일 정보를 가져오는데 실패했습니다:  ${exception.message}")
            }

            _isLoadingHolidays.value = false
        }
    }
}