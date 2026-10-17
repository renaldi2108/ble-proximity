package id.renaldi.proximity.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class ModuleGuardTask : DefaultTask() {
    @get:Input
    abstract val moduleDependencies: MapProperty<String, Set<String>>

    @get:Input
    abstract val allowedDependencies: MapProperty<String, Set<String>>

    @TaskAction
    fun verify() {
        val actual = moduleDependencies.get()
        val allowed = allowedDependencies.get()
        val violations = mutableListOf<String>()

        actual.toSortedMap().forEach { (module, deps) ->
            val permitted = allowed[module]
            if (permitted == null) {
                violations += "Modul tidak dikenal '$module'. " +
                    "Menambah modul baru wajib memperbarui settings.gradle.kts " +
                    "dan matriks di ModuleGuardConventionPlugin."
                return@forEach
            }
            deps.sorted().forEach { dep ->
                if (dep !in permitted) {
                    violations += "$module  ✗→  $dep${reasonFor(module, dep)}"
                }
            }
        }

        val missing = allowed.keys - actual.keys
        if (missing.isNotEmpty()) {
            violations += "Modul berikut ada di matriks tapi tidak ditemukan di build: " +
                missing.sorted().joinToString(", ")
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("Pelanggaran aturan dependensi modul:")
                    appendLine()
                    violations.forEach { appendLine("  • $it") }
                    appendLine()
                    appendLine("Perbaiki dependensinya — jangan melonggarkan matriksnya.")
                    appendLine("Feature hanya mengenal interface repository di :core:domain;")
                    appendLine("implementasinya tinggal di :core:data.")
                },
            )
        }

        logger.lifecycle("checkModuleDependencies: ${actual.size} modul, 0 pelanggaran.")
    }

    private fun reasonFor(module: String, dep: String): String = when {
        module.startsWith(":feature:") && dep.startsWith(":feature:") ->
            "   (feature tidak boleh saling bergantung; rute bersama tinggal di :core:navigation)"

        module.startsWith(":feature:") &&
            (dep == ":core:data" || dep == ":core:database" || dep == ":core:ble") ->
            "   (feature hanya mengenal interface repository di :core:domain)"

        dep.startsWith(":app:") ->
            "   (tidak ada modul yang boleh bergantung pada :app:*)"

        else -> "   (tidak ada di matriks dependensi)"
    }
}
