import id.renaldi.proximity.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("proximity.kmp.library")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:common"))
                implementation(project(":core:domain"))
                implementation(libs.findLibrary("koin-core").get())
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(project(":core:testing"))
                implementation(libs.findLibrary("turbine").get())
            }
        }
    }
}
