package dev.pandasystems.preprocessor.preprocessor

interface Operator {
	val symbol: String

	fun evaluate(left: VariableValue, right: VariableValue): Boolean
}

class EqualsOperator : Operator {
	override val symbol = "=="

	override fun evaluate(left: VariableValue, right: VariableValue): Boolean {
		return left == right
	}
}

class NotEqualsOperator : Operator {
	override val symbol = "!="

	override fun evaluate(left: VariableValue, right: VariableValue): Boolean {
		return left != right
	}
}

class GreaterOperator : Operator {
	override val symbol = ">"

	override fun evaluate(left: VariableValue, right: VariableValue): Boolean {
		if (left !is VariableValue.NumberValue || right !is VariableValue.NumberValue)
			throw IllegalArgumentException("Cannot compare non-numeric values: $left, $right")
		return left.value > right.value
	}
}

class LessOperator : Operator {
	override val symbol = "<"

	override fun evaluate(left: VariableValue, right: VariableValue): Boolean {
		if (left !is VariableValue.NumberValue || right !is VariableValue.NumberValue)
			throw IllegalArgumentException("Cannot compare non-numeric values: $left, $right")
		return left.value < right.value
	}
}

class GreaterOrEqualOperator : Operator {
	override val symbol = ">="

	override fun evaluate(left: VariableValue, right: VariableValue): Boolean {
		if (left !is VariableValue.NumberValue || right !is VariableValue.NumberValue)
			throw IllegalArgumentException("Cannot compare non-numeric values: $left, $right")
		return left.value >= right.value
	}
}

class LessOrEqualOperator : Operator {
	override val symbol = "<="

	override fun evaluate(left: VariableValue, right: VariableValue): Boolean {
		if (left !is VariableValue.NumberValue || right !is VariableValue.NumberValue)
			throw IllegalArgumentException("Cannot compare non-numeric values: $left, $right")
		return left.value <= right.value
	}
}