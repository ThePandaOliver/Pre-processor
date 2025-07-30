package dev.pandasystems.preprocessor

import dev.pandasystems.preprocessor.preprocessor.Preprocessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PreprocessorComparisonTest {

	@Test
	fun `test greater than operator`() {
		val preprocessor = Preprocessor().apply {
			setVariable("API_LEVEL", 30)
			setVariable("MIN_LEVEL", 21)
		}

		val source = """
            #if API_LEVEL>25
            Using newer API features
            #endif
            
            #if 15>10
            15 is greater than 10
            #endif
            
            #if MIN_LEVEL>API_LEVEL
            This should not appear
            #else
            MIN_LEVEL is not greater than API_LEVEL
            #endif
        """.trimIndent()

		val expected = """
            Using newer API features
            
            15 is greater than 10
            
            MIN_LEVEL is not greater than API_LEVEL
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test less than operator`() {
		val preprocessor = Preprocessor().apply {
			setVariable("API_LEVEL", 30)
			setVariable("MAX_LEVEL", 33)
		}

		val source = """
            #if API_LEVEL<MAX_LEVEL
            API_LEVEL is less than MAX_LEVEL
            #endif
            
            #if 5<10
            5 is less than 10
            #endif
            
            #if MAX_LEVEL<API_LEVEL
            This should not appear
            #else
            MAX_LEVEL is not less than API_LEVEL
            #endif
        """.trimIndent()

		val expected = """
            API_LEVEL is less than MAX_LEVEL
            
            5 is less than 10
            
            MAX_LEVEL is not less than API_LEVEL
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test greater than or equal operator`() {
		val preprocessor = Preprocessor().apply {
			setVariable("VERSION", 3)
			setVariable("MIN_VERSION", 3)
			setVariable("LOW_VERSION", 2)
		}

		val source = """
            #if VERSION>=MIN_VERSION
            Version is at least minimum version
            #endif
            
            #if VERSION>=LOW_VERSION
            Version is at least low version
            #endif
            
            #if 10>=10
            10 is greater than or equal to 10
            #endif
            
            #if LOW_VERSION>=VERSION
            This should not appear
            #endif
        """.trimIndent()

		val expected = """
            Version is at least minimum version
            
            Version is at least low version
            
            10 is greater than or equal to 10
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test less than or equal operator`() {
		val preprocessor = Preprocessor().apply {
			setVariable("CURRENT_VERSION", 4)
			setVariable("MAX_VERSION", 5)
			setVariable("SAME_VERSION", 4)
		}

		val source = """
            #if CURRENT_VERSION<=MAX_VERSION
            Current version is at most max version
            #endif
            
            #if CURRENT_VERSION<=SAME_VERSION
            Current version is at most same version
            #endif
            
            #if 5<=3
            This should not appear
            #else
            5 is not less than or equal to 3
            #endif
        """.trimIndent()

		val expected = """
            Current version is at most max version
            
            Current version is at most same version
            
            5 is not less than or equal to 3
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test non-numeric values in comparison operators`() {
		val preprocessor = Preprocessor().apply {
			setVariable("TEXT_VALUE", "hello")
			setVariable("NUMBER", 10)
		}

		val source = """
            #if TEXT_VALUE>5
            This should not appear (non-numeric value in >)
            #endif
            
            #if NUMBER<TEXT_VALUE
            This should not appear (non-numeric value in <)
            #endif
            
            #if TEXT_VALUE>=TEXT_VALUE
            This should not appear (non-numeric value in >=)
            #endif
            
            #if non-number<=5
            This should not appear (invalid left operand)
            #endif
            
            Regular code should appear
        """.trimIndent()

		val expected = """
            Regular code should appear
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test mixed comparison operators`() {
		val preprocessor = Preprocessor().apply {
			setVariable("API_LEVEL", 30)
			setVariable("MIN_API", 21)
			setVariable("MAX_API", 33)
			setVariable("TARGET_API", 30)
		}

		val source = """
            #if API_LEVEL>=MIN_API
            API level is at least minimum
            #if API_LEVEL<=MAX_API
            API level is at most maximum
            #if API_LEVEL==TARGET_API
            API level is exactly target
            #else
            API level is not target
            #endif
            #endif
            #endif
            
            #if API_LEVEL<MIN_API
            This should not appear
            #elif API_LEVEL>MAX_API
            This should not appear
            #else
            API level is between min and max
            #endif
        """.trimIndent()

		val expected = """
            API level is at least minimum
            API level is at most maximum
            API level is exactly target
            
            API level is between min and max
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test variable reference on both sides`() {
		val preprocessor = Preprocessor().apply {
			setVariable("VALUE_A", 10)
			setVariable("VALUE_B", 20)
			setVariable("VALUE_C", 10)
		}

		val source = """
            #if VALUE_A<VALUE_B
            A is less than B
            #endif
            
            #if VALUE_A==VALUE_C
            A equals C
            #endif
            
            #if VALUE_B<=VALUE_A
            This should not appear
            #endif
        """.trimIndent()

		val expected = """
            A is less than B
            
            A equals C
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}
}
