@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "com.eec.core"
        compileSdk = 37
        minSdk = 26
    }
    wasmJs {
        browser()
    }
    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
        androidMain.dependencies {
            implementation(libs.sqlite.bundled)
            implementation(libs.androidx.core.ktx)
        }
        wasmJsMain.dependencies {
            implementation(libs.sqlite.web)
            implementation(libs.kotlinx.browser)
        }
    }
}

dependencies {
    add("kspJvm", libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspWasmJs", libs.room.compiler)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
