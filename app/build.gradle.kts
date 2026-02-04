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

    // Read and parse JSON file with error handling
    val flavorConfigFile = file("${rootProject.projectDir}/flavor-config.json")

    if (!flavorConfigFile.exists()) {
        throw GradleException("flavor-config.json not found at: ${flavorConfigFile.absolutePath}")
    }

    // Read file content as string first to check for issues
    val jsonContent = flavorConfigFile.readText(Charsets.UTF_8).trim()

    if (jsonContent.isEmpty()) {
        throw GradleException("flavor-config.json is empty!")
    }

    // Check for null bytes
    if (jsonContent.contains('\u0000')) {
        throw GradleException("flavor-config.json contains null bytes! File may be corrupted.")
    }

    println("JSON content length: ${jsonContent.length}")
    println("First 100 chars: ${jsonContent.take(100)}")

    val jsonSlurper = JsonSlurper()
    val flavorConfig = try {
        jsonSlurper.parseText(jsonContent) as Map<String, Any>
    } catch (e: Exception) {
        throw GradleException("Failed to parse flavor-config.json: ${e.message}\nContent: ${jsonContent.take(200)}")
    }

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
        buildConfig = true
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