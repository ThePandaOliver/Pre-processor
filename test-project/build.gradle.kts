plugins {
	java
	kotlin("jvm") version "2.2.0"
	id("dev.pandasystems.preprocessor")
}

repositories {
	mavenLocal()
	mavenCentral()
}

preprocessor {
	variable("DEBUG", true)
	variable("VERSION", "1.0")
}