plugins {
    application
    id("java")
}

dependencies {
    implementation("com.google.firebase:firebase-admin:9.2.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    // fully qualified main class
    mainClass.set("com.android.adahi.seeder.SeedFirebaseAnimals")
}
