package dev.pandasystems.preprocessor

import java.util.Stack

class Preprocessor {
	private val variables = mutableMapOf<String, Any>()

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
		variables[name] = value
	}

	private val equalsPattern = Regex("""^(\S+)==(\S+)""")
	private val notEqualsPattern = Regex("""^(\S+)!=(\S+)""")
	private val booleanPattern = Regex("""^(\S+)$""")

	private val greaterPattern = Regex("""^(\S+)>(\S+)""")
	private val lessPattern = Regex("""^(\S+)<(\S+)""")
	private val greaterOrEqualsPattern = Regex("""^(\S+)>=(\S+)""")
	private val lessOrEqualsPattern = Regex("""^(\S+)<=(\S+)""")

	private fun processIf(source: String): Boolean {
		when {
			equalsPattern.containsMatchIn(source) -> {
				val (left, right) = equalsPattern.find(source)!!.destructured
				val leftValue = variables[left]?.toString() ?: left
				val rightValue = variables[right]?.toString() ?: right
				return leftValue == rightValue
			}
			notEqualsPattern.containsMatchIn(source) -> {
				val (left, right) = notEqualsPattern.find(source)!!.destructured
				val leftValue = variables[left]?.toString() ?: left
				val rightValue = variables[right]?.toString() ?: right
				return leftValue != rightValue
			}
			greaterOrEqualsPattern.containsMatchIn(source) -> {
				val (left, right) = greaterOrEqualsPattern.find(source)!!.destructured
				val leftValue = variables[left]?.toString()?.toIntOrNull() ?: left.toIntOrNull()
				val rightValue = variables[right]?.toString()?.toIntOrNull() ?: right.toIntOrNull()
				if (leftValue == null || rightValue == null)
					return false
				return leftValue >= rightValue
			}
			lessOrEqualsPattern.containsMatchIn(source) -> {
				val (left, right) = lessOrEqualsPattern.find(source)!!.destructured
				val leftValue = variables[left]?.toString()?.toIntOrNull() ?: left.toIntOrNull()
				val rightValue = variables[right]?.toString()?.toIntOrNull() ?: right.toIntOrNull()
				if (leftValue == null || rightValue == null)
					return false
				return leftValue <= rightValue
			}
			greaterPattern.containsMatchIn(source) -> {
				val (left, right) = greaterPattern.find(source)!!.destructured
				val leftValue = variables[left]?.toString()?.toIntOrNull() ?: left.toIntOrNull()
				val rightValue = variables[right]?.toString()?.toIntOrNull() ?: right.toIntOrNull()
				if (leftValue == null || rightValue == null)
					return false
				return leftValue > rightValue
			}
			lessPattern.containsMatchIn(source) -> {
				val (left, right) = lessPattern.find(source)!!.destructured
				val leftValue = variables[left]?.toString()?.toIntOrNull() ?: left.toIntOrNull()
				val rightValue = variables[right]?.toString()?.toIntOrNull() ?: right.toIntOrNull()
				if (leftValue == null || rightValue == null)
					return false
				return leftValue < rightValue
			}
			booleanPattern.containsMatchIn(source) -> {
				if (source.toBooleanStrictOrNull() != null)
					return source.toBoolean()
				return variables[source]?.toString()?.toBoolean() ?: false
			}
		}
		return false
	}
}

data class Block(
	val lines: MutableList<String> = mutableListOf(),
	val skipBlock: Boolean = false
)