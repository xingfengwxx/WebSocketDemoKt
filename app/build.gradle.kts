plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.wangxingxing.websocketdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wangxingxing.websocketdemo"
        minSdk = 24
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

    signingConfigs {
        create("release") {
            keyAlias = "jcy"
            keyPassword = "jcy123321"
            storeFile = file("jcy2024.jks")
            storePassword = "jcy123321"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation("com.gnepux:wsgo:1.0.2")
//    implementation("com.gnepux:wsgo-okwebsocket:1.0.1")

    implementation(project(":wsgo-lib"))
    implementation(project(":wsgo-okwebsocket"))

//    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("com.blankj:utilcodex:1.31.1")
}