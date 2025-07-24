package dev.pandasystems.preprocessor

import org.gradle.api.Plugin
import org.gradle.api.Project

class PreprocessorPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.tasks.register("preprocessSources") {
			doLast {
				val preprocessor = Preprocessor()
				val srcDirs = listOf("src/main/java", "src/main/kotlin")
				srcDirs.forEach { dir ->
					val srcPath = target.projectDir.resolve(dir)
					if (srcPath.exists()) {
						srcPath.walkTopDown()
							.filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
							.forEach { file ->
								val originalSource = file.readText()
								val processedSource = preprocessor.process(originalSource)
								// Write to build/preprocessed/ preserving folder structure
								val outFile = target.buildDir.resolve("preprocessed").resolve(file.relativeTo(target.projectDir))
								outFile.parentFile.mkdirs()
								outFile.writeText(processedSource)
							}
					}
				}
			}
		}
	}
}