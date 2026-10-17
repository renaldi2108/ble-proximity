import com.android.build.api.dsl.ApplicationExtension
import id.renaldi.proximity.buildlogic.BASE_PACKAGE
import id.renaldi.proximity.buildlogic.appVersionCode
import id.renaldi.proximity.buildlogic.appVersionName
import id.renaldi.proximity.buildlogic.catalogVersionInt
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.configure<ApplicationExtension> {
            namespace = BASE_PACKAGE
            compileSdk = catalogVersionInt("android-compileSdk")

            defaultConfig {
                applicationId = BASE_PACKAGE
                minSdk = catalogVersionInt("android-minSdk")
                targetSdk = catalogVersionInt("android-targetSdk")
                versionCode = appVersionCode
                versionName = appVersionName
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

            buildTypes {
                getByName("debug") {
                    applicationIdSuffix = ".debug"
                    versionNameSuffix = "-debug"
                    isMinifyEnabled = false
                }
                getByName("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                    signingConfig = signingConfigs.getByName("debug")
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            packaging {
                resources.excludes += setOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "/META-INF/versions/9/previous-compilation-data.bin",
                )
            }
        }
    }
}
