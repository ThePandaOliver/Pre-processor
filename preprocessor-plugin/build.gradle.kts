import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "2.1.0"
	id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "dev.pandasystems"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	intellijPlatform {
		defaultRepositories()
	}
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
	intellijPlatform {
		create("IU", "2025.1.4.1")
		testFramework(TestFrameworkType.Platform)

		bundledPlugin("com.intellij.java")
		bundledPlugin("org.jetbrains.kotlin")
	}
}

intellijPlatform {
	pluginConfiguration {
		ideaVersion {
			sinceBuild = "251"
		}

		changeNotes = """
            Initial version
        """.trimIndent()
	}
}

tasks {
	// Set the JVM compatibility versions
	withType<JavaCompile> {
		sourceCompatibility = "21"
		targetCompatibility = "21"
	}

	withType<KotlinCompile> {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_21
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
}
