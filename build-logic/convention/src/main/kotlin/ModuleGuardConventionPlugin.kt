import id.renaldi.proximity.buildlogic.ModuleGuardTask
import id.renaldi.proximity.buildlogic.MviGuardTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.register

class ModuleGuardConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target != target.rootProject) {
            throw GradleException("proximity.module.guard hanya boleh diterapkan di root project")
        }

        val guard = target.tasks.register<ModuleGuardTask>("checkModuleDependencies") {
            group = "verification"
            description = "Memverifikasi seluruh dependensi antar-modul terhadap matriks arsitektur."
            allowedDependencies.set(ALLOWED_MATRIX)
        }

        target.gradle.projectsEvaluated {
            val snapshot = LinkedHashMap<String, Set<String>>()
            target.subprojects.forEach { sub ->
                if (sub.subprojects.isNotEmpty()) return@forEach
                val deps = LinkedHashSet<String>()
                sub.configurations.forEach { config ->
                    config.dependencies.forEach { dep ->
                        if (dep is ProjectDependency && dep.path != sub.path) deps += dep.path
                    }
                }
                snapshot[sub.path] = deps
            }
            guard.configure { moduleDependencies.set(snapshot) }
        }

        val mviGuard = target.tasks.register<MviGuardTask>("checkMviContract") {
            group = "verification"
            description = "Memverifikasi aturan MVI yang tidak bisa dijaga compiler."

            val common = target.fileTree(target.projectDir) {
                include("core/**/src/commonMain/kotlin/**/*.kt")
                include("feature/**/src/commonMain/kotlin/**/*.kt")
                include("app/**/src/commonMain/kotlin/**/*.kt")
                exclude("**/build/**")
            }
            viewModelSources.setFrom(common.filter { it.name.endsWith("ViewModel.kt") })
            uiStateSources.setFrom(
                common.filter { file -> "UiState" in file.readText() && "class" in file.readText() },
            )
            stabilityPromisedSources.setFrom(
                common.filter { "/core/model/" in it.path },
            )
        }

        target.subprojects.forEach { sub ->
            sub.tasks.matching { it.name == "check" }.configureEach { dependsOn(guard, mviGuard) }
        }
    }

    private companion object {
        private val CORE_ALL = listOf(
            ":core:model", ":core:common", ":core:domain", ":core:ble", ":core:database",
            ":core:designsystem", ":core:fixtures", ":core:navigation", ":core:testing",
        )

        private const val UI = "presentation:ui"
        private const val NAV = "presentation:navigation"

        private val FEATURE_LAYERS: Map<String, List<String>> = mapOf(
            ":feature:scanner" to listOf(UI, NAV, "domain", "data"),
            ":feature:tracker" to listOf(UI, NAV, "domain"),
            ":feature:history" to listOf(UI, NAV, "domain", "data"),
        )

        private val FEATURE_ALL = FEATURE_LAYERS.flatMap { (feature, layers) ->
            layers.map { layer -> "$feature:$layer" }
        }

        val ALLOWED_MATRIX: Map<String, Set<String>> = buildMap {
            put(":core:model", emptySet())
            put(":core:common", setOf(":core:model"))
            put(":core:domain", setOf(":core:model", ":core:common", ":core:testing"))
            put(":core:ble", setOf(":core:model", ":core:common", ":core:domain"))
            put(":core:database", setOf(":core:model", ":core:common"))
            put(":core:designsystem", setOf(":core:model", ":core:common", ":core:fixtures"))
            put(":core:fixtures", setOf(":core:model"))
            put(":core:navigation", setOf(":core:model"))
            put(":core:testing", setOf(":core:model", ":core:common", ":core:domain"))

            FEATURE_LAYERS.forEach { (feature, layers) ->
                layers.forEach { layer ->
                    val allowed = when (layer) {
                        "domain" -> setOf(
                            ":core:model", ":core:common", ":core:domain", ":core:testing",
                        )

                        "data" -> setOf(
                            ":core:model", ":core:common", ":core:domain",
                            ":core:ble", ":core:database", ":core:testing",
                        )

                        UI -> setOf(
                            ":core:model", ":core:common", ":core:domain",
                            ":core:designsystem", ":core:fixtures", ":core:testing",
                        ) + "$feature:domain"

                        else -> setOf(
                            ":core:model", ":core:common", ":core:designsystem",
                            ":core:navigation", ":core:testing",
                        ) + "$feature:$UI"
                    }
                    put("$feature:$layer", allowed)
                }
            }

            put(":app:shared", (CORE_ALL + FEATURE_ALL).toSet())
            put(":app:android", setOf(":app:shared"))
        }
    }
}
