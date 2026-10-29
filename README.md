# 며칠이지? (What's the date today?)

---

## 기능

### D-day 관리
- D-day 추가/편집/삭제
- 실시간 카운트다운
- 라벨 및 제목 설정

### 날짜 계산 옵션
- 일반 날짜 계산
- 공휴일 제외
- 안식일 설정 및 제외
- 특정 요일 제외

### 위젯 기능
- 배경화면에 위젯 설정

### 휴무일 설정
- 한국 공휴일 API 통합
- 공휴일 자동 업데이트
- 안식일(휴가) 설정

### 커스터마이징
- 6가지 색상 테마
- 정렬 기능

### 데이터 저장
- SharedPreferences 사용
- 자동 저장
- 앱 재시작 후에도 유지

---

## 🛠️ 기술 스택

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Storage:** SharedPreferences + Gson
- **Network:** Retrofit2 (공휴일 API)
- **Design:** Material Design 3

---

## 📁 프로젝트 구조

```
app/src/main/java/com/example/ddayapp/
├── MainActivity.kt                      # 메인 액티비티
├── data/
│   ├── DDay.kt                          # D-day 데이터 모델
│   ├── Holiday.kt                       # 공휴일 데이터 모델
│   └── PrefsHelper.kt                   # 자동 새로고침 모델
│   └── Settings.kt                      # 설정 데이터 모델
├── ui/
│   ├── DdayScreen.kt                    # 메인 화면
│   ├── components/
│   │   ├── DdayCard.kt                  # D-day 카드 컴포넌트
│   │   ├── AddEditDialog.kt             # 추가/편집 다이얼로그
│   │   └── SettingsDialog.kt            # 설정 다이얼로그
│   └── theme/
│       ├── Color.kt                     # 색상 정의
│       ├── Theme.kt                     # 테마 설정
│       └── Type.kt                      # 타이포그래피
├── utils/
│   ├── DateCalculator.kt                # 날짜 계산 로직
│   ├── HolidayApi.kt                    # 공휴일 API
│   └── PreferencesHelper.kt             # 데이터 저장
├── viewmodel/
│   └── DdayViewModel.kt                 # ViewModel
└── widget/
    ├── DdayWidgetConfigActivity.kt      # Widget 액티비티
    └── DdayWidgetProvider.kt            # Widget 실행

```

---
