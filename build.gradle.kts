import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings

plugins {
	kotlin("jvm") version "2.0.21"
	`kotlin-dsl`
	`java-gradle-plugin`
	idea
	alias(libs.plugins.ideaExt)
	`maven-publish`
}

group = "dev.pandasystems"
version = "0.1-POC.3"

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(kotlin("gradle-plugin"))
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

publishing {
	publications {
		create<MavenPublication>("preprocessor") {
			from(components["java"])
		}
	}
}