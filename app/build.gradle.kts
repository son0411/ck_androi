plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Plugin để sử dụng Google Services (Firebase)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.cktimviec"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cktimviec"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android libraries
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.1.0") // Firebase Auth
    implementation("com.google.firebase:firebase-storage:20.3.0") // Firebase Storage
    implementation("com.github.bumptech.glide:glide:4.15.1") // Glide for image loading
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Firebase Database
    implementation("com.google.firebase:firebase-database:20.3.0")

    // Google Play Services Authentication
    implementation("com.google.android.gms:play-services-auth:20.0.0")

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.8.0")

    // Firebase BOM (quản lý phiên bản cho các dịch vụ Firebase)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics-ktx") // Firebase Analytics

    // Lifecycle and ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // Activity and Fragment
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Áp dụng plugin Google Services để sử dụng Firebase
apply(plugin = "com.google.gms.google-services")
