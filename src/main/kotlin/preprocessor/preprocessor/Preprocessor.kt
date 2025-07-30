package dev.pandasystems.preprocessor.preprocessor

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
	private val expressionParser = ExpressionParser(operators, variableStore)

	fun process(source: String): String {
		val lines = source.lines()
		val blockStack = Stack<Block>().apply { push(Block()) }

		for (line in lines) {
			val trimmed = line.trim()
			if (trimmed.startsWith("#")) {
				// If the line is preprocessor
				when {
					trimmed.startsWith("#if ") -> {
						val condition = processIf(trimmed.substringAfter("#if ").trim())
						blockStack.push(Block(skipBlock = !condition, conditionMet = condition))
					}

					trimmed.startsWith("#elif ") -> {
						val prevBlock = blockStack.pop()
						if (!prevBlock.skipBlock) {
							// Previous block was included, so skip all remaining elif/else blocks
							blockStack.peek().lines.addAll(prevBlock.lines)
							blockStack.push(Block(skipBlock = true, conditionMet = true))
						} else if (prevBlock.conditionMet) {
							// A previous condition in this chain was already met, skip this elif
							blockStack.push(Block(skipBlock = true, conditionMet = true))
						} else {
							// Previous block was skipped and no condition met yet, evaluate this condition
							val condition = processIf(trimmed.substringAfter("#elif ").trim())
							blockStack.push(Block(skipBlock = !condition, conditionMet = condition))
						}
					}

					trimmed.startsWith("#else") -> {
						val prevBlock = blockStack.pop()
						if (!prevBlock.skipBlock) {
							// Previous block was included, so skip the else block
							blockStack.peek().lines.addAll(prevBlock.lines)
							blockStack.push(Block(skipBlock = true, conditionMet = true))
						} else if (prevBlock.conditionMet) {
							// A previous condition in this chain was already met, skip this else
							blockStack.push(Block(skipBlock = true, conditionMet = true))
						} else {
							// No previous condition was met, include this else block
							blockStack.push(Block(skipBlock = false, conditionMet = true))
						}
					}

					trimmed == "#endif" -> {
						val prevBlock = blockStack.pop()
						if (!prevBlock.skipBlock) {
							blockStack.peek().lines += prevBlock.lines
						}
					}
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

	private fun processIf(expression: String): Boolean {
		return expressionParser.parse(expression)
	}
}

data class Block(
	val lines: MutableList<String> = mutableListOf(),
	val skipBlock: Boolean = false,
	val conditionMet: Boolean = false
)
