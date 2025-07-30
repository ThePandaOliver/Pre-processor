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
			when {
				trimmed.startsWith("#if ") -> {
					blockStack.push(Block(skipBlock = !processIf(trimmed.substringAfter("#if ").trim())))
				}

				trimmed.startsWith("#elif ") -> {
					val prevBlock = blockStack.pop()
					if (!prevBlock.skipBlock) {
						blockStack.peek().lines.addAll(prevBlock.lines)
						blockStack.push(Block(skipBlock = true)) // Already processed a block, skip this one
					} else {
						// Previous block was skipped, evaluate this condition
						blockStack.push(Block(skipBlock = !processIf(trimmed.substringAfter("#elif ").trim())))
					}
				}

				trimmed.startsWith("#else") -> {
					val prevBlock = blockStack.pop()
					if (!prevBlock.skipBlock) {
						blockStack.peek().lines.addAll(prevBlock.lines)
						blockStack.push(Block(skipBlock = true)) // Already processed a block, skip this one
					} else {
						// Previous block was skipped, include this one
						blockStack.push(Block(skipBlock = false))
					}
				}

				trimmed == "#endif" -> {
					val prevBlock = blockStack.pop()
					if (!prevBlock.skipBlock) {
						blockStack.peek().lines.addAll(prevBlock.lines)
					}
				}

				else -> {
					if (!blockStack.peek().skipBlock) {
						blockStack.peek().lines.add(line)
					}
				}
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
	val skipBlock: Boolean = false
)