import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import id.renaldi.proximity.buildlogic.catalogVersionInt
import id.renaldi.proximity.buildlogic.iosTargetsEnabled
import id.renaldi.proximity.buildlogic.libs
import id.renaldi.proximity.buildlogic.moduleNamespace
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension>(
                "androidLibrary",
            ) {
                namespace = moduleNamespace
                compileSdk = catalogVersionInt("android-compileSdk")
                minSdk = catalogVersionInt("android-minSdk")
                withHostTest {}
            }

            jvm {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            if (iosTargetsEnabled) {
                iosArm64()
                iosSimulatorArm64()
            }

            jvmToolchain(17)

            sourceSets.getByName("commonMain").dependencies {
                api(libs.findLibrary("kotlinx-coroutines-core").get())
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(kotlin("test"))
                implementation(libs.findLibrary("kotlinx-coroutines-test").get())
            }
        }
    }
}
