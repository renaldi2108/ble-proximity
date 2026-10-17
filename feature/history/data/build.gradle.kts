plugins {
    id("proximity.kmp.feature.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.database)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }
        jvmTest.dependencies {
            implementation(projects.core.database)
            implementation(libs.sqldelight.driver.jvm)
        }
    }
}
