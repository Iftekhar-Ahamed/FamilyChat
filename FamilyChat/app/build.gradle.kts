plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.familychat"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.familychat"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.android.volley:volley:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("org.slf4j:slf4j-jdk14:1.7.25")
    implementation ("com.microsoft.signalr:signalr:7.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}