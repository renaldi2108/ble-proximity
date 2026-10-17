plugins {
    id("proximity.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.tracker.domain)
        }
    }
}
