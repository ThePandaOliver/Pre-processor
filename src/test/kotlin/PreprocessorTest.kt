package dev.pandasystems.preprocessor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PreprocessorTest {

	private val preprocessor = Preprocessor().apply {
		setVariable("TestTrue", true)
		setVariable("TestTrue2", true)
		setVariable("TestFalse", false)
	}

	@Test
	fun `test basic preprocessing with if true condition`() {
		val source = """
            line before condition
            #if true
            this line should be included
            #endif
            line after condition
        """.trimIndent()

		val expected = """
            line before condition
            this line should be included
            line after condition
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test basic preprocessing with if false condition`() {
		val source = """
            line before condition
            #if false
            this line should be excluded
            #endif
            line after condition
        """.trimIndent()

		val expected = """
            line before condition
            line after condition
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test preprocessing with if-else conditions`() {
		val source = """
            line before condition
            #if false
            this line should be excluded
            #else 
            this line should be included
            #endif
            line after condition
        """.trimIndent()

		val expected = """
            line before condition
            this line should be included
            line after condition
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test preprocessing with if-elif-else conditions`() {
		val source = """
            #if false
            not included
            #elif true
            this should be included
            #else 
            not included
            #endif
            last line
        """.trimIndent()

		val expected = """
            this should be included
            last line
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}

	@Test
	fun `test nested if conditions`() {
		val source = """
            #if true
            outer if
            #if false
            nested if (excluded)
            #endif
            outer if again
            #endif
            last line
        """.trimIndent()

		val expected = """
            outer if
            outer if again
            last line
        """.trimIndent()

		assertEquals(expected, preprocessor.process(source))
	}
}
