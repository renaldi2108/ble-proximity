package id.renaldi.proximity.buildlogic

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class MviGuardTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val viewModelSources: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val uiStateSources: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val stabilityPromisedSources: ConfigurableFileCollection

    @TaskAction
    fun verify() {
        val violations = buildList {
            addAll(checkSingleEntryPoint())
            addAll(checkImmutableUiState())
            addAll(checkStabilityPromise())
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("Pelanggaran aturan MVI:")
                    appendLine()
                    violations.forEach { appendLine("  • $it") }
                    appendLine()
                    appendLine(RATIONALE)
                },
            )
        }

        val scanned = viewModelSources.files.size + uiStateSources.files.size +
            stabilityPromisedSources.files.size
        logger.lifecycle("checkMviContract: $scanned berkas dipindai, 0 pelanggaran.")
    }

    private fun checkSingleEntryPoint(): List<String> = buildList {
        viewModelSources.files.forEach { file ->
            val text = file.readText()
            VIEW_MODEL_CLASS.findAll(text).forEach { match ->
                val className = match.groupValues[1]
                val body = text.substring(match.range.first, classBodyEnd(text, match.range.first))
                body.lineSequence().forEach { line ->
                    if (PUBLIC_MEMBER_FUN.containsMatchIn(line)) {
                        add(
                            "${file.name} :: $className   ${line.trim()}" +
                                "\n      (MVI-01 — jadikan private dan tambahkan Action di kontrak; " +
                                "onEvent harus satu-satunya pintu masuk)",
                        )
                    }
                }
            }
        }
    }

    private fun checkImmutableUiState(): List<String> = buildList {
        uiStateSources.files.forEach { file ->
            val text = file.readText()
            val declarations = DECLARATION.findAll(text).toList()

            UI_STATE_REFERENCE.findAll(text).forEach { reference ->
                val declaration = declarations.lastOrNull { it.range.first < reference.range.first }
                    ?: return@forEach
                val start = declaration.range.first
                val between = text.substring(start, reference.range.first)
                if (between.count { it == '<' } > between.count { it == '>' }) return@forEach

                val region = text.substring(start, regionEnd(text, reference.range.last + 1))
                if (MUTABLE_PROPERTY.containsMatchIn(region)) {
                    add(
                        "${file.name} :: ${declaration.groupValues[1]} punya properti var" +
                            "\n      (MVI-02 — UiState harus immutable; state berubah lewat " +
                            "setState { copy(...) }, bukan lewat mutasi di tempat)",
                    )
                }
            }
        }
    }

    private fun regionEnd(text: String, from: Int): Int {
        val lineEnd = text.indexOf('\n', from).let { if (it == -1) text.length else it }
        val hasBody = text.substring(from, lineEnd).contains('{')
        if (!hasBody) return lineEnd
        val bodyEnd = text.indexOf("\n}\n", from)
        return if (bodyEnd == -1) text.length else bodyEnd
    }

    private fun checkStabilityPromise(): List<String> = buildList {
        stabilityPromisedSources.files.forEach { file ->
            file.readLines().forEachIndexed { index, line ->
                val reason = when {
                    MUTABLE_PROPERTY.containsMatchIn(line) -> "properti var"
                    MUTABLE_COLLECTION.containsMatchIn(line) -> "koleksi mutable"
                    else -> return@forEachIndexed
                }
                add(
                    "${relativeName(file)}:${index + 1}  $reason -> ${line.trim()}" +
                        "\n      (MVI-03 — paket ini dijanjikan stabil di compose-stability.conf; " +
                        "melanggarnya membuat recomposition terlewat diam-diam, bukan sekadar lambat)",
                )
            }
        }
    }

    private fun relativeName(file: File): String =
        file.path.substringAfter("kotlin/id/renaldi/proximity/", file.name)

    private fun classBodyEnd(text: String, from: Int): Int {
        val end = text.indexOf("\n}\n", from)
        return if (end == -1) text.length else end
    }

    private companion object {
        val VIEW_MODEL_CLASS = Regex("""^(?:\w+ )*class (\w*ViewModel)\b""", RegexOption.MULTILINE)

        val UI_STATE_REFERENCE = Regex(""":\s*(?:Loadable)?UiState\b""")

        val DECLARATION = Regex(
            """^[ \t]*(?:\w+ )*(?:class|interface|object)\s+(\w+)""",
            RegexOption.MULTILINE,
        )

        val PUBLIC_MEMBER_FUN =
            Regex("""^ {4}(?!private |protected |internal |override )(?:\w+ )*fun (?!onEvent\b)\w""")

        val MUTABLE_PROPERTY = Regex("""(?:^ {4}|\(|,\s*)(?:@\w+\s+)?var \w""", RegexOption.MULTILINE)

        val MUTABLE_COLLECTION =
            Regex("""\b(?:MutableList|MutableMap|MutableSet|ArrayList|HashMap|HashSet)\b""")

        const val RATIONALE =
            "MVI-01 onEvent adalah satu-satunya pintu masuk ke ViewModel.\n" +
                "MVI-02 UiState immutable.\n" +
                "MVI-03 Paket yang dijanjikan stabil di compose-stability.conf benar-benar immutable."
    }
}
