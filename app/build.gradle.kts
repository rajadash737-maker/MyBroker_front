
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

val localBaseUrl = localProps.getProperty("BASE_URL") ?: "http://10.44.238.22:8000/"
val localRazorpayKeyId = localProps.getProperty("RAZORPAY_KEY_ID") ?: "rzp_test_SZkZj6jXN4IGTj"
val releaseBaseUrl = "https://api.mybroker.com/"
val releaseRazorpayKeyId = localProps.getProperty("RAZORPAY_KEY_ID") ?: "rzp_test_SZkZj6jXN4IGTj"
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val hasReleaseSigning = keystoreProps.getProperty("storeFile")?.isNotBlank() == true &&
    keystoreProps.getProperty("storePassword")?.isNotBlank() == true &&
    keystoreProps.getProperty("keyAlias")?.isNotBlank() == true &&
    keystoreProps.getProperty("keyPassword")?.isNotBlank() == true

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$localBaseUrl\"")
        buildConfigField("String", "RAZORPAY_KEY_ID", "\"$localRazorpayKeyId\"")
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "BASE_URL", "\"$localBaseUrl\"")
            buildConfigField("String", "RAZORPAY_KEY_ID", "\"$localRazorpayKeyId\"")
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            buildConfigField("String", "BASE_URL", "\"$releaseBaseUrl\"")
            buildConfigField("String", "RAZORPAY_KEY_ID", "\"$releaseRazorpayKeyId\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            pickFirsts += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/io.netty.versions.properties"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material")
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    implementation("com.google.android.material:material:1.12.0")

    implementation("com.google.maps.android:maps-compose:6.4.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.razorpay:checkout:1.6.33")
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 🔥 Firebase (add this)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")
}
