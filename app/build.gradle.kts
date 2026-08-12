import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.hmibrahimsarkar.kabboloker_brakkhakbi"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.hmibrahimsarkar.kabboloker_brakkhakbi"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    
    // অ্যাপের নাম সেট করুন (strings.xml-এ ওভাররাইড হবে)
    resourceConfigurations.add("bn") // শুধু বাংলা রিসোর্স রাখতে চাইলে
  }

  signingConfigs {
    create("release") {
        storeFile = file("${rootDir}/kabyalok-release.jks")
        storePassword = System.getenv("STORE_PASSWORD") ?: "08556665"
        keyAlias = "kabyalok"
        keyPassword = System.getenv("KEY_PASSWORD") ?: "08556665"
    }
    create("debugConfig") {
        storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
      
      // 🔥 APK ফাইলের নাম কাস্টমাইজ করুন - Release
      applicationVariants.all {
        outputs.all {
          val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
          output.outputFileName = "kabboloker_brakkhakbi-${defaultConfig.versionName}.apk"
        }
      }
    }
    debug { 
      signingConfig = signingConfigs.getByName("debugConfig")
      
      // 🔥 APK ফাইলের নাম কাস্টমাইজ করুন - Debug
      applicationVariants.all {
        outputs.all {
          val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
          output.outputFileName = "kabboloker_brakkhakbi-debug.apk"
        }
      }
    }
  }
  
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  
  buildFeatures {
    compose = true
    buildConfig = true
  }
  
  testOptions { 
    unitTests { isIncludeAndroidResources = true } 
  }
}

// Configure the Secrets Gradle Plugin
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

googleServices { 
  missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN 
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.retrofit)
  
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
