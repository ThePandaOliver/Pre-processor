package dev.pandasystems.preprocessor.preprocessor

class ExpressionParser(
	private val operators: List<Operator>,
	private val variableStore: VariableStore
) {
	fun parse(expression: String): Boolean {
		val sortedOperators = operators.sortedByDescending { it.symbol.length }

		for (operator in sortedOperators) {
			val (left, right) = expression.split(operator.symbol, limit = 2).map {
				val trimmed = it.trim()
				when {
					trimmed.startsWith('"') && trimmed.endsWith('"') -> VariableValue.StringValue(trimmed.substring(1, trimmed.length - 1))
					trimmed.toFloatOrNull() != null -> VariableValue.NumberValue(trimmed.toFloat())
					trimmed.toBooleanStrictOrNull() != null -> VariableValue.BooleanValue(trimmed.toBooleanStrict())
					else -> variableStore.getValue(trimmed) ?: throw IllegalArgumentException("Unknown value or variable: $trimmed")
				}
			}
			return operator.evaluate(left, right)
		}

		throw IllegalArgumentException("Invalid expression: $expression")
	}
}
