plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.moutamid.daiptv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moutamid.daiptv"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "DA_IPTV-$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { viewBinding = true }

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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("org.simpleframework:simple-xml:2.7.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")

    implementation("com.fxn769:stash:1.3.2")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.iammannan:TranslateAPI:1.1")

    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.mindorks.android:prdownloader:0.6.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.activity:activity:1.8.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.github.hty527.iPlayer:widget:2.1.26.1")
    implementation("com.github.hty527.iPlayer:iplayer:2.1.26.1")
    implementation("com.github.hty527.iPlayer:cache:2.1.26.1")
//    implementation("com.github.hty527.iPlayer:ijk:2.1.20")
//    implementation("com.github.hty527.iPlayer:exo:2.1.20")


    implementation("androidx.paging:paging-runtime:3.2.1")

    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}