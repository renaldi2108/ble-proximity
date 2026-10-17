plugins {
    id("proximity.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.scanner.domain)
        }
    }
}
