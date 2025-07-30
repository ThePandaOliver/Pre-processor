package dev.pandasystems.preprocessor.preprocessor.keywords

import dev.pandasystems.preprocessor.preprocessor.Block
import dev.pandasystems.preprocessor.preprocessor.Preprocessor
import java.util.Stack

interface PreprocessorKeyword {
	val keyword: String
	fun process(line: String, processor: Preprocessor, blockStack: Stack<Block>): Boolean
}