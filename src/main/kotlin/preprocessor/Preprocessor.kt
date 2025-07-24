package dev.pandasystems.preprocessor

import java.util.Stack

class Preprocessor {
	fun process(source: String): String {
		val lines = source.lines()

		val blockStack = Stack<Block>().apply { push(Block()) }

		for (line in lines) {
			val trimmed = line.trim()
			when {
				trimmed.startsWith("#if ") -> {
					// TODO: make actual if logic
					blockStack.push(Block(skipBlock = !trimmed.substringAfter("#if ").trim().toBoolean()))
				}

				trimmed.startsWith("#elif ") -> {
					val prevBlock = blockStack.pop()
					if (!prevBlock.skipBlock)
						blockStack.peek().lines += prevBlock.lines
					// TODO: make actual if logic
					blockStack.push(Block(skipBlock = !prevBlock.skipBlock && !trimmed.substringAfter("#elif ").trim().toBoolean()))
				}

				trimmed.startsWith("#else") -> {
					val prevBlock = blockStack.pop()
					if (!prevBlock.skipBlock)
						blockStack.peek().lines += prevBlock.lines
					blockStack.push(Block(skipBlock = !prevBlock.skipBlock))
				}

				trimmed == "#endif" -> {
					val prevBlock = blockStack.pop()
					if (!prevBlock.skipBlock)
						blockStack.peek().lines += prevBlock.lines

				}

				else -> blockStack.peek().lines += line
			}
		}
		return blockStack.peek().lines.joinToString("\n")
	}
}

data class Block(
	val lines: MutableList<String> = mutableListOf(),
	val skipBlock: Boolean = false
)