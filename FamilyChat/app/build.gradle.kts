plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.familychat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.familychat"
        minSdk = 28
        targetSdk = 34
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
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.microsoft.signalr:signalr:7.0.0")
    testImplementation("junit:junit:4.13.2")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("org.slf4j:slf4j-jdk14:1.7.25")
    implementation ("org.greenrobot:eventbus:3.2.0")
    implementation("com.android.volley:volley:1.2.0")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.13.0")

}