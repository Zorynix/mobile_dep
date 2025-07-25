apply(plugin = "com.google.protobuf")

plugins {
    alias(libs.plugins.android.application)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id(libs.plugins.jetbrains.kotlin.android.get().pluginId)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    alias(libs.plugins.compose.compiler)
    id("com.google.protobuf")
}

android {
    namespace = "com.diploma.work"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.diploma.work"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildToolsVersion = "35.0.0"
}

dependencies {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains:annotations:26.0.2")
            exclude(group = "org.jetbrains", module = "annotations-java5")
        }
    }

    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.espresso.remote)
    val composeBom = platform("androidx.compose:compose-bom:2025.06.01")
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.ui.test.junit4)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.activity.compose.v1100)
    implementation(libs.androidx.runtime.rxjava2)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.junit.ktx)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.core.splashscreen)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.orhanobut.logger)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.coil.compose)
    implementation(libs.ui)
    implementation(libs.coil.network.okhttp)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)

    kapt(libs.kotlinx.metadata.jvm)
    implementation(libs.android.maps.utils)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.protobuf.java)
    implementation(libs.javax.annotation.api)
    implementation(libs.androidx.compose.material3.material32)
    implementation(libs.kotlin.reflect)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose.v310)

    implementation(libs.compose.markdown)
    implementation(libs.highlights)

    implementation(libs.markwon.core)
    implementation(libs.markwon.syntax)
    implementation(libs.markwon.editor)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.grpc.stub)
    testImplementation(kotlin("test"))
}
kapt {
    correctErrorTypes = true
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.2"
    }
    plugins {
        create("java")
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.72.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
            it.builtins {
                create("java")
            }
        }
    }
}