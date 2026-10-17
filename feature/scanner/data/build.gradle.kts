plugins {
    id("proximity.kmp.feature.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ble)
        }
    }
}
