public class TestClass {
    public static void main(String[] args) {
        System.out.println("Starting application...");
        
        #if DEBUG
        System.out.println("Debug mode is enabled");
        System.out.println("Detailed logging active");
        #else
        System.out.println("Production mode");
        #endif
        
        #if VERSION==1.0
        System.out.println("Version 1.0 features enabled");
        #elif VERSION==2.0
        System.out.println("Version 2.0 features enabled");
        #else
        System.out.println("Unknown version");
        #endif

        System.out.println("Application initialized");
    }
}