import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("proximity.kmp.compose")
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "SharedApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.domain)
            implementation(projects.core.ble)
            implementation(projects.core.database)
            implementation(projects.core.designsystem)
            implementation(projects.core.navigation)

            implementation(projects.feature.scanner.data)
            implementation(projects.feature.scanner.domain)
            implementation(projects.feature.scanner.presentation.navigation)
            implementation(projects.feature.scanner.presentation.ui)

            implementation(projects.feature.tracker.domain)
            implementation(projects.feature.tracker.presentation.navigation)
            implementation(projects.feature.tracker.presentation.ui)

            implementation(projects.feature.history.data)
            implementation(projects.feature.history.domain)
            implementation(projects.feature.history.presentation.navigation)
            implementation(projects.feature.history.presentation.ui)

            implementation(libs.bundles.feature.runtime)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.activity.compose)
        }
    }
}
