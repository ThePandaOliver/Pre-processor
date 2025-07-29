package dev.pandasystems.preprocessor

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import java.io.File

class PreprocessorPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val extension = target.extensions.create("preprocessor", PreprocessorExtension::class.java)
		val preprocessTask = target.tasks.register<PreprocessorTask>("preprocessSources", target)

		// Set up dependencies after all plugins are applied
		target.afterEvaluate {
			// Make all compilation tasks depend on preprocessing
			target.tasks.withType<AbstractCompile>().configureEach {
				dependsOn(preprocessTask)
			}

			// Also handle Kotlin compilation tasks specifically
			target.tasks.matching { it.name.startsWith("compile") && it.name.contains("Kotlin") }
				.configureEach { dependsOn(preprocessTask) }
		}
	}
}

open class PreprocessorExtension(
	private val project: Project,
) {
	internal val preprocessor: Preprocessor = Preprocessor()

	fun variable(name: String, value: Any) {
		preprocessor.setVariable(name, value)
	}
}