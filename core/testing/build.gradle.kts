plugins {
    id("proximity.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(projects.core.common)
            api(projects.core.domain)
            api(libs.kotlinx.coroutines.test)
            api(libs.turbine)
        }
    }
}
