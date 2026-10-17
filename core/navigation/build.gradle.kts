plugins {
    id("proximity.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(libs.androidx.navigation.compose)
            api(libs.kotlinx.serialization.json)
        }
    }
}
