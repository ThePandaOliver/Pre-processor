package dev.pandasystems.preprocessor.preprocessor.keywords

import dev.pandasystems.preprocessor.preprocessor.Block
import dev.pandasystems.preprocessor.preprocessor.Preprocessor
import java.util.Stack

class IfKeyword : PreprocessorKeyword {
	override val keyword = "if"

	override fun process(line: String, processor: Preprocessor, blockStack: Stack<Block>): Boolean {
		val condition = processor.expressionParser.parse(line)
		blockStack.push(Block(skipBlock = !condition, conditionMet = condition))
		return true
	}
}

class ElifKeyword : PreprocessorKeyword {
	override val keyword = "elif"

	override fun process(line: String, processor: Preprocessor, blockStack: Stack<Block>): Boolean {
		val prevBlock = blockStack.pop()
		if (!prevBlock.skipBlock) {
			blockStack.peek().lines.addAll(prevBlock.lines)
			blockStack.push(Block(skipBlock = true, conditionMet = true))
		} else if (prevBlock.conditionMet) {
			blockStack.push(Block(skipBlock = true, conditionMet = true))
		} else {
			val condition = processor.expressionParser.parse(line)
			blockStack.push(Block(skipBlock = !condition, conditionMet = condition))
		}
		return true
	}
}

class ElseKeyword : PreprocessorKeyword {
	override val keyword = "else"

	override fun process(line: String, processor: Preprocessor, blockStack: Stack<Block>): Boolean {
		val prevBlock = blockStack.pop()
		if (!prevBlock.skipBlock) {
			blockStack.peek().lines.addAll(prevBlock.lines)
			blockStack.push(Block(skipBlock = true, conditionMet = true))
		} else if (prevBlock.conditionMet) {
			blockStack.push(Block(skipBlock = true, conditionMet = true))
		} else {
			blockStack.push(Block(skipBlock = false, conditionMet = true))
		}
		return true
	}
}

class EndifKeyword : PreprocessorKeyword {
	override val keyword = "endif"

	override fun process(line: String, processor: Preprocessor, blockStack: Stack<Block>): Boolean {
		val prevBlock = blockStack.pop()
		if (!prevBlock.skipBlock) {
			blockStack.peek().lines += prevBlock.lines
		}
		return true
	}
}
