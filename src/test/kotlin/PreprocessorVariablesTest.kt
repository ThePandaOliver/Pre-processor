package dev.pandasystems.preprocessor

import dev.pandasystems.preprocessor.preprocessor.Preprocessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PreprocessorVariablesTest {

    @Test
    fun `test variable in conditional`() {
        val preprocessor = Preprocessor().apply {
            setVariable("DEBUG", true)
        }
        
        val source = """
            #if DEBUG
            Debug code is included
            #endif
            Regular code
        """.trimIndent()

        val expected = """
            Debug code is included
            Regular code
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }

    @Test
    fun `test multiple variables in conditions`() {
        val preprocessor = Preprocessor().apply {
            setVariable("ANDROID", true)
            setVariable("IOS", false)
        }
        
        val source = """
            Common code
            #if ANDROID
            Android-specific code
            #endif
            #if IOS
            iOS-specific code
            #endif
            More common code
        """.trimIndent()

        val expected = """
            Common code
            Android-specific code
            More common code
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }
    
    @Test
    fun `test equality operator`() {
        val preprocessor = Preprocessor().apply {
            setVariable("VERSION", "1.0")
            setVariable("BUILD_TYPE", "debug")
        }
        
        val source = """
            #if VERSION==1.0
            Current version code
            #endif
            #if BUILD_TYPE==release
            Release-only code
            #elif BUILD_TYPE==debug
            Debug-only code
            #endif
        """.trimIndent()

        val expected = """
            Current version code
            Debug-only code
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }
    
    @Test
    fun `test inequality operator`() {
        val preprocessor = Preprocessor().apply {
            setVariable("API_LEVEL", "30")
        }
        
        val source = """
            #if API_LEVEL!=29
            Using newer API features
            #endif
            
            #if API_LEVEL!=30
            Not the current API level
            #else
            Current API level
            #endif
        """.trimIndent()

        val expected = """
            Using newer API features
            
            Current API level
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }
    
    @Test
    fun `test complex nested conditionals with variables`() {
        val preprocessor = Preprocessor().apply {
            setVariable("PREMIUM", true)
            setVariable("DARK_THEME", true)
            setVariable("VERSION", "2.0")
        }
        
        val source = """
            App starts
            #if PREMIUM
            Premium features enabled
            #if DARK_THEME
            Dark theme applied
            #else
            Light theme applied
            #endif
            #if VERSION==1.0
            Old version handling
            #elif VERSION==2.0
            New version features
            #endif
            #else
            Free version limitations
            #endif
            App continues
        """.trimIndent()

        val expected = """
            App starts
            Premium features enabled
            Dark theme applied
            New version features
            App continues
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }
    
    @Test
    fun `test non-boolean variables`() {
        val preprocessor = Preprocessor().apply {
            setVariable("COUNT", 5)
            setVariable("NAME", "TestApp")
        }
        
        val source = """
            #if COUNT==5
            Count is 5
            #endif
            
            #if NAME==TestApp
            App name is TestApp
            #endif
            
            #if NAME==OtherApp
            This should not appear
            #endif
        """.trimIndent()

        val expected = """
            Count is 5
            
            App name is TestApp
        """.trimIndent()

        assertEquals(expected.trim(), preprocessor.process(source).trim())
    }
    
    @Test
    fun `test variable not defined`() {
        val preprocessor = Preprocessor()
        
        val source = """
            #if UNDEFINED_VAR
            This should not appear
            #else
            Undefined variable handling
            #endif
        """.trimIndent()

        val expected = """
            Undefined variable handling
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }
    
    @Test
    fun `test bug in not equals pattern`() {
        val preprocessor = Preprocessor().apply {
            setVariable("PLATFORM", "android")
        }
        
        val source = """
            #if PLATFORM!=ios
            Not iOS platform
            #else
            iOS platform
            #endif
        """.trimIndent()

        val expected = """
            Not iOS platform
        """.trimIndent()

        assertEquals(expected, preprocessor.process(source))
    }
}
