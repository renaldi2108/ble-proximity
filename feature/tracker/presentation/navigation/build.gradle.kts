plugins {
    id("proximity.kmp.feature.navigation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.tracker.presentation.ui)
        }
    }
}
