plugins {
    id("proximity.android.application")
}

dependencies {
    implementation(projects.app.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    debugImplementation(libs.leakcanary.android)
}
