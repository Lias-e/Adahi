import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.adahi"
    compileSdk = 36

    defaultConfig {
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }

        fun resolveConfigValue(key: String): String? {
            return (project.findProperty(key) as String?)
                ?: System.getenv(key)
                ?: localProperties.getProperty(key)
        }

        val chargilySecretKey = resolveConfigValue("CHARGILY_SECRET_KEY")
            ?: resolveConfigValue("CHARGILY_PRIVATE_KEY")
            ?: ""
        val chargilyPublicKey = resolveConfigValue("CHARGILY_PUBLIC_KEY")
            ?: ""
        val chargilyApiBaseUrl = resolveConfigValue("CHARGILY_API_BASE_URL")
            ?: "https://pay.chargily.net/test/api/v2"

        applicationId = "adhahi.com"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "CHARGILY_SECRET_KEY", "\"${chargilySecretKey.replace("\"", "\\\"")}\"")
        buildConfigField("String", "CHARGILY_API_BASE_URL", "\"${chargilyApiBaseUrl.replace("\"", "\\\"")}\"")
        buildConfigField("String", "CHARGILY_PUBLIC_KEY", "\"${chargilyPublicKey.replace("\"", "\\\"")}\"")
        buildConfigField("String", "CHARGILY_PRIVATE_KEY", "\"${chargilySecretKey.replace("\"", "\\\"")}\"")
        buildConfigField("String", "CHARGILY_APP_RETURN_URL", "\"${project.findProperty("CHARGILY_APP_RETURN_URL") ?: "https://adahi.app/payment-return"}\"")

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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // RecyclerView for animal list
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Import the Firebase BoM (Bill of Materials) to manage Firebase SDK versions
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    
    // Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Custom Tabs / Browser integration for secure in-app browser
    implementation("androidx.browser:browser:1.5.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}