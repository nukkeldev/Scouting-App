import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
            freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.bumble.appyx.utils.multiplatform.Parcelize")
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.coil.compose)
        }

        commonMain.dependencies {
            implementation(libs.webcam.capture)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(libs.androidx.compiler)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.bumble.appyx.navigation)
            implementation(libs.qrcode.kotlin)
            implementation(libs.okhttp)
            implementation(libs.json)
            implementation(libs.bumble.appyx.navigation)
            implementation(libs.material3)
            api(libs.backstack)
        }
    }
}

android {
    namespace = "org.tahomarobotics.scouting"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.tahomarobotics.scouting"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/NOTICE.md"
            pickFirsts += "META-INF/NOTICE.md"
        }

    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}