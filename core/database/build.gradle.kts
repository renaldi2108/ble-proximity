plugins {
    id("proximity.sqldelight")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            implementation(projects.core.common)
        }
    }
}
