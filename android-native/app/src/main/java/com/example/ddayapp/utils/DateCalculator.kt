package com.example.ddayapp.utils

import java.text.SimpleDateFormat
import java.util.*

// 날짜 및 디데이 계산 관련 및 유틸리티 객체
object DateCalculator {

    // 날짜 파싱용 기본 포맷 : yyyy-mm-dd
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * D-Day 계산
     * @param targetDate 목표 날짜 (yyyy-MM-dd)
     * @param excludePublicHolidays 공휴일 제외 여부
     * @param excludeCustomDays 휴무일 제외 여부
     * @param excludedWeekdays 제외할 요일 Set (1=일, 2=월, ..., 7=토)
     * @param publicHolidays 공휴일 Set (yyyy-MM-dd 형식)
     * @param customDays 휴무일 Set (yyyy-MM-dd 형식)
     * @return D-Day 문자열 (예: 미래 : "D-30", 과거 : "D-Day", 오늘 : "D+15")
     */

    fun calculateDDay(
        targetDate: String,
        excludePublicHolidays:  Boolean,
        excludeCustomDays: Boolean,
        excludedWeekdays: Set<Int> = emptySet(),
        publicHolidays: Set<String> = emptySet(),
        customDays: Set<String> = emptySet()
    ): String {

        // 오늘 날짜 : 시간 제거 작업 (날짜 비교, 정확성 확보)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 목표 날짜 파싱 및 시간 제거
        val target = Calendar.getInstance().apply {
            time = dateFormat.parse(targetDate) ?: Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 오늘과 목표일이 같으면 "D-Day" 문구 표시
        if (isSameDay(today, target)) return "D-Day"

        // 미래면 +1, 과거면 -1 방향으로 이동
        val direction = if (target.after(today)) 1 else -1

        // 날짜를 이동시키며 계산할 임시 캘린더
        val current = today.clone() as Calendar
        var count = 0

        // 날짜를 하루씩 이동하며 계산
        while (true) {
            // 먼저 날짜 이동
            current.add(Calendar.DAY_OF_MONTH, direction)

            // 목표일에 도달하면 중단 (목표일은 카운트에 포함하지 않음)
            if (isSameDay(current, target)) break

            // 요일 표시 : 1-일, 2-월, 3-화, 4-수, 5-목, 6-금, 7-토
            val dayOfWeek = current.get(Calendar.DAY_OF_WEEK)  // 1=일, 2=월, ..., 7=토

            // 요일 제외 체크
            if (excludedWeekdays.contains(dayOfWeek)) {
                continue
            }

            // 공휴일 제외 체크
            if (excludePublicHolidays && isHoliday(current, publicHolidays)) {
                continue
            }

            // 휴무일 제외 체크
            if (excludeCustomDays && isHoliday(current, customDays)) {
                continue
            }

            // 제외 조건에 해당하지 않으면 카운트 증가
            count++
        }

        return if (direction > 0) "D-$count" else "D+${Math.abs(count)}"
    }


    //두 Calendar가 같은 날짜인지 확인
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    //날짜를 포맷팅 (yyyy. MM.dd.)
    fun formatDate(dateStr:  String): String {
        return try {
            val date = dateFormat.parse(dateStr) ?: return dateStr
            val cal = Calendar.getInstance().apply { time = date }
            val year = cal.get(Calendar.YEAR)
            val month = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            val day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
            "$year.$month.$day."
        } catch (e: Exception) {
            dateStr
        }
    }

     //오늘 날짜를 문자열로 반환 (yyyy-MM-dd)
    fun getTodayString(): String {
        val cal = Calendar.getInstance()
        return String.format(
            "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // 날짜 표시 형식 변환 (yyyy-MM-dd → yyyy.MM.dd)
    fun formatDisplayDate(date: String): String {
        return try {
            val parts = date.split("-")
            "${parts[0]}.${parts[1]}.${parts[2]}"
        } catch (e:  Exception) {
            date
        }
    }

    /**
     * 공휴일/휴무일인지 확인
     * @param calendar 확인할 날짜
     * @param holidays 날짜 Set (yyyy-MM-dd 형식)
     * @return 해당하면 true
     */
    private fun isHoliday(
        calendar: Calendar,
        holidays: Set<String>
    ): Boolean {
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
        val date = "$year-$month-$day"
        return date in holidays
    }
}