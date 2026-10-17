plugins {
    id("proximity.kmp.feature.navigation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.history.presentation.ui)
        }
    }
}
