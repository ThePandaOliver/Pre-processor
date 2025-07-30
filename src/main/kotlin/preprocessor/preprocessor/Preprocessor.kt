package dev.pandasystems.preprocessor.preprocessor

import dev.pandasystems.preprocessor.preprocessor.keywords.ElifKeyword
import dev.pandasystems.preprocessor.preprocessor.keywords.ElseKeyword
import dev.pandasystems.preprocessor.preprocessor.keywords.EndifKeyword
import dev.pandasystems.preprocessor.preprocessor.keywords.IfKeyword
import java.util.Stack

class Preprocessor {
	private val variableStore = VariableStore()
	private val operators = listOf(
		EqualsOperator(),
		NotEqualsOperator(),
		GreaterOperator(),
		LessOperator(),
		GreaterOrEqualOperator(),
		LessOrEqualOperator()
	)
	val expressionParser = ExpressionParser(operators, variableStore)
	private val keywords = listOf(
		IfKeyword(),
		ElifKeyword(),
		ElseKeyword(),
		EndifKeyword()
	)


	fun process(source: String): String {
		val lines = source.lines()
		val blockStack = Stack<Block>().apply { push(Block()) }

		for (line in lines) {
			val trimmed = line.trim()
			if (trimmed.startsWith("#")) {
				val parts = trimmed.substring(1).split(' ', limit = 2)
				val keywordName = parts[0]
				val expression = parts.getOrNull(1) ?: ""
				val keyword = keywords.find { it.keyword == keywordName } ?: throw IllegalArgumentException("Unknown keyword: $keywordName")
				try {
					keyword.process(expression, this, blockStack)
				} catch (e: Exception) {
					throw IllegalArgumentException("Failed to process keyword $keywordName: ${e.message}", e)
				}
			} else {
				val currentBlock = blockStack.peek()
				if (!currentBlock.skipBlock)
					currentBlock.lines += line
			}
		}

		// Trim trailing empty lines but keep internal ones
		val result = blockStack.peek().lines.joinToString("\n")
		return result.trim()
	}

	fun setVariable(name: String, value: Any) {
		when (value) {
			is String -> variableStore.setString(name, value)
			is Number -> variableStore.setNumber(name, value.toFloat())
			is Boolean -> variableStore.setBoolean(name, value)
			else -> variableStore.setString(name, value.toString())
		}
	}

	fun removeVariable(name: String) {
		variableStore.remove(name)
	}
}

data class Block(
	val lines: MutableList<String> = mutableListOf(),
	val skipBlock: Boolean = false,
	val conditionMet: Boolean = false
)
