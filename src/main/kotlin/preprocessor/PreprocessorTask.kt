package dev.pandasystems.preprocessor

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File
import javax.inject.Inject

open class PreprocessorTask @Inject constructor(private val project: Project) : DefaultTask() {
	init {
		group = "build"
		description = "Runs the Preprocessor"
	}

	@get:Internal
	val buildDir = project.layout.buildDirectory.get().asFile
	@get:Internal
	val preprocessor = project.extensions.getByType(PreprocessorExtension::class.java).preprocessor

	@TaskAction
	fun run() {
		println("=== PreprocessorTask: Starting preprocessing ===")

		// Process both Java and Kotlin source sets separately
		// First handle Java source sets (from java plugin)
		val sourceSetContainer = project.extensions.findByType(SourceSetContainer::class.java)
		if (sourceSetContainer != null) {
			println("Processing Java source sets")
			sourceSetContainer.forEach { sourceSet ->
				println("Processing Java source set: ${sourceSet.name}")
				processSourceDirectory(sourceSet.java, "java")
				processSourceDirectory(sourceSet.resources, "resources")
			}
		}

		// Then handle Kotlin source sets (from kotlin plugin)
		val kotlinExtension = project.extensions.findByType(KotlinProjectExtension::class.java)
		if (kotlinExtension != null) {
			println("Processing Kotlin source sets")
			kotlinExtension.sourceSets.forEach { sourceSet ->
				println("Processing Kotlin source set: ${sourceSet.name}")
				processSourceDirectory(sourceSet.kotlin, "kotlin")
				// Don't process resources again if we already did it above
				if (sourceSetContainer == null) {
					processSourceDirectory(sourceSet.resources, "resources")
				}
			}
		}


		println("=== PreprocessorTask: Finished preprocessing ===")
	}

	private fun processSourceDirectory(source: SourceDirectorySet, type: String) {
		val originalSrcDirs = source.srcDirs.toSet()
		println("Original $type source dirs: $originalSrcDirs")
		
		if (originalSrcDirs.isEmpty()) {
			println("No source directories found for $type, skipping")
			return
		}
		
		val newSrcDirs = mutableSetOf<File>()

		originalSrcDirs.forEach { srcDir ->
			if (!srcDir.exists()) {
				println("Source directory does not exist: $srcDir")
				return@forEach
			}
			
			val outputDir = srcDir.relativeTo(project.projectDir)
				.let { buildDir.resolve("preprocessed-sources/$it") }
			
			println("Processing $srcDir -> $outputDir")

			var processedFiles = 0
			srcDir.walkTopDown()
				.filter { it.isFile }
				.forEach { file ->
					val outputFile = outputDir.resolve(file.relativeTo(srcDir))
					outputFile.parentFile.mkdirs()
					
					val originalContent = file.readText()
					val processedContent = preprocessor.process(originalContent)
					outputFile.writeText(processedContent)
					
					if (originalContent != processedContent) {
						println("Preprocessed file: ${file.relativeTo(project.projectDir)} -> ${outputFile.relativeTo(project.projectDir)}")
					}
					processedFiles++
				}
			
			println("Processed $processedFiles files from $srcDir")
			newSrcDirs.add(outputDir)
		}

		println("Setting new $type source dirs to: $newSrcDirs")
		// Atomically replace the old source dirs with the new, preprocessed ones
		source.setSrcDirs(newSrcDirs)
		println("New $type source dirs after setting: ${source.srcDirs}")
	}
}