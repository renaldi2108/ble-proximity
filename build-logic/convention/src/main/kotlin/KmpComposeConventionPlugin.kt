import id.renaldi.proximity.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("DEPRECATION")
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("proximity.kmp.library")
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        val compose: ComposePlugin.Dependencies =
            extensions.getByType<ComposeExtension>().dependencies

        extensions.configure<ComposeCompilerGradlePluginExtension> {
            stabilityConfigurationFiles.add(
                isolated.rootProject.projectDirectory.file("compose-stability.conf"),
            )

            if (providers.gradleProperty("composeReports").orNull == "true") {
                val dir = layout.buildDirectory.dir("compose-reports")
                reportsDestination.set(dir)
                metricsDestination.set(dir)
            }
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(libs.findLibrary("compose-ui-tooling-preview").get())
            }
            sourceSets.getByName("androidMain").dependencies {
                implementation(libs.findLibrary("compose-ui-tooling").get())
            }
        }
    }
}
