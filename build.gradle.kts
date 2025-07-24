import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings

plugins {
	kotlin("jvm") version "2.2.0"
	`kotlin-dsl`
	idea
	alias(libs.plugins.ideaExt)
}

group = "dev.pandasystems"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	gradleApi()
	gradleTestKit()
	gradleKotlinDsl()
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(21)
}

gradlePlugin {
	plugins {
		create("preprocessor") {
			id = "dev.pandasystems.preprocessor"
			implementationClass = "dev.pandasystems.preprocessor.PreprocessorPlugin"
		}
	}
}

idea {
	module {
		settings {
			val packagePrefixStr = "dev.pandasystems"
			packagePrefix["src/main/kotlin"] = packagePrefixStr
			packagePrefix["src/main/java"] = packagePrefixStr
		}
	}
}