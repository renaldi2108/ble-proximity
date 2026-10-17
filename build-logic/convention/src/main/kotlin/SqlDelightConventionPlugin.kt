import app.cash.sqldelight.gradle.SqlDelightExtension
import id.renaldi.proximity.buildlogic.BASE_PACKAGE
import id.renaldi.proximity.buildlogic.iosTargetsEnabled
import id.renaldi.proximity.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class SqlDelightConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("proximity.kmp.library")
        pluginManager.apply("app.cash.sqldelight")

        extensions.configure<SqlDelightExtension> {
            databases.create("ProximityDatabase") {
                packageName.set("$BASE_PACKAGE.core.database")
                schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
                deriveSchemaFromMigrations.set(false)
                generateAsync.set(false)
            }
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.findLibrary("sqldelight-runtime").get())
                implementation(libs.findLibrary("sqldelight-coroutines").get())
            }
            sourceSets.getByName("androidMain").dependencies {
                implementation(libs.findLibrary("sqldelight-driver-android").get())
            }
            sourceSets.getByName("jvmMain").dependencies {
                implementation(libs.findLibrary("sqldelight-driver-jvm").get())
            }
            if (iosTargetsEnabled) {
                sourceSets.matching { it.name == "iosMain" }.configureEach {
                    dependencies {
                        implementation(libs.findLibrary("sqldelight-driver-native").get())
                    }
                }
            }
        }
    }
}
