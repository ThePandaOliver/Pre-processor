fun main(args: Array<String>) {
	println("Starting application...")

	#if DEBUG == true
	println("Debug mode is enabled");
	println("Detailed logging active");
	#else
	println("Production mode");
	#endif

	#if VERSION == "1.0"
	println("Version 1.0 features enabled");
	#elif VERSION == "2.0"
	println("Version 2.0 features enabled");
	#else
	println("Unknown version");
	#endif

	println("Application initialized");
}