import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "id.renaldi.proximity.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.plugin.android.gradle)
    compileOnly(libs.plugin.kotlin.gradle)
    compileOnly(libs.plugin.compose.gradle)
    compileOnly(libs.plugin.compose.compiler.gradle)
    compileOnly(libs.plugin.sqldelight.gradle)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "proximity.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "proximity.kmp.compose"
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = "proximity.kmp.feature"
            implementationClass = "KmpFeatureConventionPlugin"
        }
        register("kmpFeatureDomain") {
            id = "proximity.kmp.feature.domain"
            implementationClass = "KmpFeatureDomainConventionPlugin"
        }
        register("kmpFeatureData") {
            id = "proximity.kmp.feature.data"
            implementationClass = "KmpFeatureDataConventionPlugin"
        }
        register("kmpFeatureNavigation") {
            id = "proximity.kmp.feature.navigation"
            implementationClass = "KmpFeatureNavigationConventionPlugin"
        }
        register("androidApplication") {
            id = "proximity.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("sqldelight") {
            id = "proximity.sqldelight"
            implementationClass = "SqlDelightConventionPlugin"
        }
        register("moduleGuard") {
            id = "proximity.module.guard"
            implementationClass = "ModuleGuardConventionPlugin"
        }
    }
}
