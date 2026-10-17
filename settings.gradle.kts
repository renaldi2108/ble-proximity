@file:Suppress("UnstableApiUsage")

rootProject.name = "proximity"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app:android")
include(":app:shared")

include(":core:model")
include(":core:common")
include(":core:domain")
include(":core:ble")
include(":core:database")
include(":core:designsystem")
include(":core:fixtures")
include(":core:navigation")
include(":core:testing")

include(":feature:scanner:data")
include(":feature:scanner:domain")
include(":feature:scanner:presentation:navigation")
include(":feature:scanner:presentation:ui")

include(":feature:tracker:domain")
include(":feature:tracker:presentation:navigation")
include(":feature:tracker:presentation:ui")

include(":feature:history:data")
include(":feature:history:domain")
include(":feature:history:presentation:navigation")
include(":feature:history:presentation:ui")
