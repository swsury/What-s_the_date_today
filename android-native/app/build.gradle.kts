import java.util.Properties

plugins {
    // Android 앱 모듈
    id("com.android.application")
    // Kotlin Android 지원
    id("org.jetbrains.kotlin.android")
}

// local.properties 로드
// API 키, 비밀값 등을 코드에 직접 넣지 않고 local.properties 파일에서 불러오기 위한 설정
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
// local.properties 파일이 존재할 경우에만 로드
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    // 앱 패키지 네임스페이스
    namespace = "com.example.ddayapp"
    // 컴파일 SDK 버전 (최신 API 기능 사용 기준)
    compileSdk = 34

    defaultConfig {
        // 앱 고유 ID (Play Store 식별값)
        applicationId = "com.example.ddayapp"
        // 최소 지원 Android 버전 (Android 7.0)
        minSdk = 24
        // 타겟 Android 버전
        targetSdk = 34
        // 앱 버전 관리
        versionCode = 1
        versionName = "1.0"
        // 테스트 실행 Runner
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // VectorDrawable 지원 설정 (하위 버전 호환)
        vectorDrawables {
            useSupportLibrary = true
        }

        // BuildConfig에 API 키 주입
        // local.properties의 값을 BuildConfig로 전달, 코드에서는 BuildConfig.KOREA_HOLIDAY_API_KEY로 접근 가능
        // 목적 : API 키를 Git에 노출하지 않기 위함
        buildConfigField(
            "String",
            "KOREA_HOLIDAY_API_KEY",
            "\"${localProperties["KOREA_HOLIDAY_API_KEY"]}\""
        )
    }

    // 빌드 타입 설정
    buildTypes {
        // 릴리즈 빌드 설정 (배포용)
        release {
            // 코드 난독화/최적화 비활성화 (현재는 OFF)
            isMinifyEnabled = false
            // Proguard 규칙 설정
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Java & Kotlin 설정
    compileOptions {
        // Java 17 사용
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // desugaring 활성화 : 최신 Java API를 낮은 Android 버전에서도 사용 가능하게 함
        // 예: java.time (LocalDate 등)
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        // Kotlin도 JVM 17 타겟
        jvmTarget = "17"
    }

    // 빌드 기능 활성화
    buildFeatures {
        // BuildConfig 클래스 생성 (API 키 등 포함)
        buildConfig = true
        // Jetpack Compose 사용
        compose = true
    }

    // Compose 설정
    composeOptions {
        // Compose 컴파일러 버전
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    // 패키징 설정
    packaging {
        resources {
            // 라이선스 충돌 파일 제외
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// 의존성 관리
dependencies {
    // Desugaring 라이브러리 : Java 최신 API를 구버전 Android에서 사용 가능
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Core Android 라이브러리

    // Kotlin 확장 기능
    implementation("androidx.core:core-ktx:1.12.0")
    // Lifecycle 관리
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    // Compose Activity 지원
    implementation("androidx.activity:activity-compose:1.8.2")

    // Jetpack Compose UI

    // 버전 통합 관리
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    // 기본 UI
    implementation("androidx.compose.ui:ui")
    // 그래픽 처리
    implementation("androidx.compose.ui:ui-graphics")
    // 미리보기
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Material3 디자인
    implementation("androidx.compose.material3:material3")
    // 아이콘
    implementation("androidx.compose.material:material-icons-extended")

    // ViewModel (상태 관리)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // JSON 파싱
    implementation("com.google.code.gson:gson:2.10.1")

    // 네트워크 통신 (API) : Retrofit

    // HTTP 통신
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // JSON 변환
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glance (Widget)

    // AppWidget Compose 버전
    implementation("androidx.glance:glance-appwidget:1.1.0")
    // Material3 스타일 지원
    implementation("androidx.glance:glance-material3:1.1.0")


    // Test

    // 단위 테스트
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // 디버그 전용 (UI 확인, 테스트)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
