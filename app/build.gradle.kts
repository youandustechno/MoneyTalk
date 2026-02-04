import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.clovis.moneytalk"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.clovis.moneytalk"
        minSdk = 28
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    flavorDimensions += "version"

    // Read and parse JSON file
    val flavorConfigFile = file("${rootProject.projectDir}/flavor-config.json")
    val jsonSlurper = JsonSlurper()
    val flavorConfig = jsonSlurper.parse(flavorConfigFile) as Map<String, Any>
    val flavors = flavorConfig["flavors"] as List<Map<String, Any>>

    productFlavors {
        flavors.forEach { flavor ->
            create(flavor["key"] as String) {
                dimension = "version"

                // Set unique application ID for each flavor
                applicationId = flavor["applicationId"] as String

                versionNameSuffix = "-${flavor["key"]}"

                // Set app name from JSON
                resValue("string", "app_name", flavor["appName"] as String)

                // Add build config fields
                buildConfigField("String", "FLAVOR_KEY", "\"${flavor["key"]}\"")
                buildConfigField("String", "FLAVOR_NAME", "\"${flavor["appName"]}\"")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true  // Add this to enable BuildConfig generation
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}