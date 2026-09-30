package com.example.ddayapp.utils

import com.example.ddayapp.data.Holiday
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.ddayapp.BuildConfig


//한국천문연구원 특일(공휴일) 정보 API Retrofit 인터페이스

interface HolidayApiService {
    @GET("service/SpcdeInfoService/getRestDeInfo")
    suspend fun getHolidays(
        @Query("solYear") year: String, // 조회할 연도 (yyyy)
        @Query("ServiceKey", encoded = true) serviceKey: String, // 공공데이터포털에서 발급받은 인증키
        @Query("numOfRows") numOfRows: Int = 50, // 한 페이지당 결과 수
        @Query("pageNo") pageNo: Int = 1, // 페이지 번호
        @Query("_type") type: String = "json" // 응답 형식 (json)
    ): HolidayResponse
}

// API 응답 매핑용 모델들

// 최상위 응답 객체
data class HolidayResponse(
    @SerializedName("response") val response: Response?
)

// response 내부 객체
data class Response(
    @SerializedName("body") val body: Body?
)

// body 내부 객체
data class Body(
    @SerializedName("items") val items: Items?
)

// items 내부 객체
data class Items(
    @SerializedName("item") val item: List<HolidayItem>?
)

// 실제 공휴일 데이터 항목
data class HolidayItem(
    @SerializedName("locdate") val locdate: Int,
    @SerializedName("dateName") val dateName: String?
)

// API 호출 관리 객체
object HolidayApi {
    
    private const val BASE_URL = "https://apis.data.go.kr/B090041/openapi/" // 공공데이터 API 기본 URL
    private const val SERVICE_KEY = BuildConfig.KOREA_HOLIDAY_API_KEY // BuildConfig에 저장된 API 키

    // Retrofit 인스턴스 생성
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API 서비스 객체 생성
    private val service = retrofit.create(HolidayApiService::class.java)
    
    /**
     * 공휴일 가져오기
     * @param year 연도 (예: "2024")
     * @return 공휴일 리스트
     */
    suspend fun fetchHolidays(year: String): Result<List<Holiday>> {
        return try {
            // API 호출
            val response = service.getHolidays(
                year = year,
                serviceKey = SERVICE_KEY
            )

            // 중첩된 응답 구조에서 item 리스트 추출
            val items = response.response?.body?.items?.item ?: emptyList()

            // API 데이터를 앱 내부 모델(Holiday)로 변환
            val holidays = items.mapNotNull { item ->
                val locdate = item.locdate.toString()

                // 날짜 형식 검증 (yyyyMMdd)
                if (locdate.length == 8) {
                    val date = "${locdate.substring(0, 4)}-${locdate.substring(4, 6)}-${locdate.substring(6, 8)}"
                    Holiday(date = date, name = item.dateName ?: "")
                } else {
                    null
                }
            }
            
            Result.success(holidays)
        } catch (e: Exception) {
            // 네트워크 / 파싱 오류 처리
            Result.failure(e)
        }
    }
}
