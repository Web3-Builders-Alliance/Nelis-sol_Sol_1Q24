plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.8.10"
}

android {
    namespace = "spl.cards.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "spl.cards.app"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata")

    // Navigation Component
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.5")

    // Accompanist - SwipeRefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.17.0")

    // Koin Dependency Injection
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")

    // Coil Compose
    implementation("io.coil-kt:coil-compose:1.3.2")

    // Lottie Compose
    implementation("com.airbnb.android:lottie-compose:4.0.0")

    // OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Web3 Mnemonic Utils
    implementation("org.bitcoinj:bitcoinj-core:0.16.2")

    // Ed25519 curve
    implementation("net.i2p.crypto:eddsa:0.3.0")

    implementation("cash.z.ecc.android:kotlin-bip39:1.0.7")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.7.1")

    // SolanaKT
    implementation("com.github.metaplex-foundation:SolanaKT:2.1.1")

    // Borsh
    implementation("com.github.metaplex-foundation:kborsh:0.1.0b1") {
        exclude(group = "com.github.metaplex-foundation.kborsh", module = "kborsh-jvm")
    }

    // SDK
    implementation("com.github.metaplex-foundation:metaplex-android:1.3.4") {
        exclude(group = "com.github.metaplex-foundation.kborsh", module = "kborsh-jvm")
    }

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}