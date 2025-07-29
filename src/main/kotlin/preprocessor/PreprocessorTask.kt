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

	private val buildDir = project.layout.buildDirectory.get().asFile
	private val preprocessor = project.extensions.getByType(PreprocessorExtension::class.java).preprocessor

	private val processedDirectories = mutableSetOf<File>()

	@TaskAction
	fun run() {
		project.logger.lifecycle("=== PreprocessorTask: Starting preprocessing ===")

		processedDirectories.clear()

		val sourceSetContainer = project.extensions.findByType(SourceSetContainer::class.java)
		if (sourceSetContainer != null) {
			project.logger.lifecycle("Processing Java source sets")
			sourceSetContainer.forEach { sourceSet ->
				project.logger.lifecycle("Processing Java source set: ${sourceSet.name}")
				processSourceDirectory(sourceSet.java, "java")
				processSourceDirectory(sourceSet.resources, "resources")
			}
		}

		val kotlinExtension = project.extensions.findByType(KotlinProjectExtension::class.java)
		if (kotlinExtension != null) {
			project.logger.lifecycle("Processing Kotlin source sets")
			kotlinExtension.sourceSets.forEach { sourceSet ->
				project.logger.lifecycle("Processing Kotlin source set: ${sourceSet.name}")
				processSourceDirectory(sourceSet.kotlin, "kotlin")
				processSourceDirectory(sourceSet.resources, "resources")
			}
		}

		project.logger.lifecycle("=== PreprocessorTask: Finished preprocessing ===")
	}

	private fun processSourceDirectory(source: SourceDirectorySet, type: String) {
		val originalSrcDirs = source.srcDirs.toSet()
		project.logger.lifecycle("Original $type source dirs: $originalSrcDirs")
		
		if (originalSrcDirs.isEmpty()) {
			project.logger.lifecycle("No source directories found for $type, skipping")
			return
		}
		
		val newSrcDirs = mutableSetOf<File>()

		originalSrcDirs.forEach { srcDir ->
			// Skip if this directory has already been processed
			if (srcDir in processedDirectories) {
				project.logger.lifecycle("Directory $srcDir already processed, skipping for $type")
				// Still need to add the output directory to newSrcDirs
				val outputDir = srcDir.relativeTo(project.projectDir)
					.let { buildDir.resolve("preprocessed-sources/$it") }
				newSrcDirs.add(outputDir)
				return@forEach
			}

			// Skip if the directory doesn't exist
			if (!srcDir.exists()) {
				project.logger.lifecycle("Source directory does not exist: $srcDir")
				return@forEach
			}
			
			val outputDir = srcDir.relativeTo(project.projectDir)
				.let { buildDir.resolve("preprocessed-sources/$it") }
			
			project.logger.lifecycle("Processing $srcDir -> $outputDir")

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
						project.logger.lifecycle("Preprocessed file: ${file.relativeTo(project.projectDir)} -> ${outputFile.relativeTo(project.projectDir)}")
					}
					processedFiles++
				}
			
			project.logger.lifecycle("Processed $processedFiles files from $srcDir")
			newSrcDirs += outputDir
			processedDirectories += outputDir
		}

		project.logger.lifecycle("Setting new $type source dirs to: $newSrcDirs")
		// Atomically replace the old source dirs with the new, preprocessed ones
		source.setSrcDirs(newSrcDirs)
		project.logger.lifecycle("New $type source dirs after setting: ${source.srcDirs}")
	}
}