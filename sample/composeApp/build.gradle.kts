import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(project(":vaultkmp"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.runtime)
        }
        
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "in.sitharaj.vaultkmp.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    defaultConfig {
        applicationId = "in.sitharaj.vaultkmp.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "in.sitharaj.vaultkmp.sample.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "VaultKMP Sample"
            packageVersion = "1.0.0"
        }
    }
}
