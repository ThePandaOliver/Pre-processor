package dev.pandasystems.preprocessor

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class PreprocessorPlugin : Plugin<Project> {
	private val preprocessor = Preprocessor()

	override fun apply(target: Project) {
		val extension = target.extensions.create("preprocessor", PreprocessorExtension::class.java, target, preprocessor)

		target.tasks.register("preprocessSources") {
			doLast {
				val javaExtension = target.extensions.getByType(JavaPluginExtension::class.java)
				javaExtension.sourceSets.forEach { sourceSet ->
					println("Java source set: ${sourceSet.name}")
					sourceSet.allSource.srcDirs.forEach { dir ->
						println("  - ${dir.path}")

						val sources = if (dir.exists()) {
							dir.walkTopDown().filter { it.isFile }.toList()
						} else {
							emptyList()
						}

						val outputDir = target.layout.buildDirectory.get().asFile.resolve("generated/sources/preprocessor/${sourceSet.name}")

						sources.forEach { sourceFile ->
							val relativePath = sourceFile.relativeTo(dir)

							if (sourceFile.extension == "java") {
								println("  - Preprocessing ${sourceFile.path}")
								val outputFile = outputDir.resolve(relativePath)
								outputFile.parentFile.mkdirs()
								preprocessor.process(sourceFile.readText()).let { text ->
									outputFile.writeText(text)
								}
							} else {
								println("  - Copying ${sourceFile.path}")
								val outputFile = outputDir.resolve(relativePath)
								outputFile.parentFile.mkdirs()
								sourceFile.copyTo(outputFile, overwrite = true)
							}
						}
					}
				}
			}
		}
	}
}

open class PreprocessorExtension(private val project: Project, private val preprocessor: Preprocessor) {
	fun variable(name: String, value: Any) {
		preprocessor.setVariable(name, value)
	}
}