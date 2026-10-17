plugins {
    id("proximity.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(projects.core.common)
            implementation(projects.core.fixtures)
        }
    }
}
