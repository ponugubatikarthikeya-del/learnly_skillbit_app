import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Load local.properties for Real AdMob IDs
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// These read from local.properties for Production
val realAppId = localProperties.getProperty("admob.app.id") ?: ""
val realBannerId = localProperties.getProperty("admob.banner.id") ?: ""
val realInterstitialId = localProperties.getProperty("admob.interstitial.id") ?: ""
val realRewardedId = localProperties.getProperty("admob.rewarded.id") ?: ""
val realNativeId = localProperties.getProperty("admob.native.id") ?: ""
val realAppOpenId = localProperties.getProperty("admob.appopen.id") ?: ""

// Standard Google Test IDs for safe development
val testAppId = "ca-app-pub-3940256099942544~3347511713"
val testBannerId = "ca-app-pub-3940256099942544/6300978111"
val testInterstitialId = "ca-app-pub-3940256099942544/1033173712"
val testRewardedId = "ca-app-pub-3940256099942544/5224354917"
val testNativeId = "ca-app-pub-3940256099942544/2247696110"
val testAppOpenId = "ca-app-pub-3940256099942544/9257395921"

android {
    namespace = "com.factifyft.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.factifyft.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = false
        abortOnError = false
    }

    buildTypes {
        debug {
            // Use resValue so it's available in AndroidManifest.xml as @string/admob_app_id
            resValue("string", "admob_app_id", testAppId)
            
            buildConfigField("String", "AD_UNIT_ID", "\"$testAppOpenId\"")
            buildConfigField("String", "BANNER_ID", "\"$testBannerId\"")
            buildConfigField("String", "INTERSTITIAL_ID", "\"$testInterstitialId\"")
            buildConfigField("String", "REWARDED_ID", "\"$testRewardedId\"")
            buildConfigField("String", "NATIVE_ID", "\"$testNativeId\"")
        }
        release {
            // Use resValue so it's available in AndroidManifest.xml as @string/admob_app_id
            resValue("string", "admob_app_id", realAppId)

            buildConfigField("String", "AD_UNIT_ID", "\"$realAppOpenId\"")
            buildConfigField("String", "BANNER_ID", "\"$realBannerId\"")
            buildConfigField("String", "INTERSTITIAL_ID", "\"$realInterstitialId\"")
            buildConfigField("String", "REWARDED_ID", "\"$realRewardedId\"")
            buildConfigField("String", "NATIVE_ID", "\"$realNativeId\"")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Ad libraries
    implementation(libs.play.services.ads)
    implementation(libs.lifecycle.process)
    implementation(libs.core.splashscreen)
}
