package id.renaldi.proximity.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

const val BASE_PACKAGE: String = "id.renaldi.proximity"

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.moduleNamespace: String
    get() = BASE_PACKAGE + path.replace(":", ".")

val Project.iosTargetsEnabled: Boolean
    get() = (findProperty("proximity.enableIosTargets") as? String)?.toBooleanStrictOrNull() ?: true

fun Project.catalogVersion(alias: String): String = libs.findVersion(alias).get().requiredVersion

fun Project.catalogVersionInt(alias: String): Int = catalogVersion(alias).toInt()

val Project.appVersionName: String
    get() = property("proximity.versionName") as String

val Project.appVersionCode: Int
    get() = (property("proximity.versionCode") as String).toInt()
