plugins {
    id("proximity.kmp.feature.navigation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.scanner.presentation.ui)
        }
    }
}
