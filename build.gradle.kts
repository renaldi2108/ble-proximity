plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.sqldelight) apply false

    id("proximity.module.guard")
}

tasks.register("syncIosVersion") {
    val versionName = providers.gradleProperty("proximity.versionName")
    val versionCode = providers.gradleProperty("proximity.versionCode")
    val target = layout.projectDirectory.file("iosApp/Version.xcconfig")

    inputs.property("versionName", versionName)
    inputs.property("versionCode", versionCode)
    outputs.file(target)

    doLast {
        target.asFile.writeText(
            "MARKETING_VERSION = ${versionName.get()}\n" +
                "CURRENT_PROJECT_VERSION = ${versionCode.get()}\n",
        )
    }
}
