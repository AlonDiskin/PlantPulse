plugins {
    alias(libs.plugins.android.library)
    id("com.google.devtools.ksp")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.alon.plantpulse.plantsdetail.featuretest"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        dataBinding = true
    }
    testOptions {
        unitTests {
            this.isIncludeAndroidResources = true
            unitTests.isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Project modules
    implementation(project(":features:userGarden:di"))

    // Android core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Local testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.fragment.testing)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.espresso.core)
    testImplementation("androidx.test.espresso:espresso-contrib:3.7.0")
    testImplementation(libs.greenCoffee)
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt)
    testImplementation(libs.hiltTest)
    kspTest(libs.hilt.android.compiler)
    testImplementation(libs.room.runtime)
    testImplementation(libs.room.ktx)
    testImplementation(libs.room.paging)
    kspTest(libs.room.compiler)
    testImplementation(libs.room.testing)
    testImplementation(libs.kotlinx.coroutines.test)

}