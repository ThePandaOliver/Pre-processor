package dev.pandasystems.preprocessor.preprocessor

sealed class VariableValue(private val value: Any?) {
    data class StringValue(val value: String) : VariableValue(value)
    data class NumberValue(val value: Float) : VariableValue(value)
    data class BooleanValue(val value: Boolean) : VariableValue(value)

    override fun equals(other: Any?): Boolean {
        if (other is VariableValue)
            return value == other.value
        return super.equals(other)
    }

	override fun hashCode(): Int {
		return javaClass.hashCode()
	}
}

class VariableStore {
    private val variables = mutableMapOf<String, VariableValue>()
    
    fun setString(name: String, value: String) {
        variables[name] = VariableValue.StringValue(value)
    }
    
    fun setNumber(name: String, value: Float) {
        variables[name] = VariableValue.NumberValue(value)
    }
    
    fun setBoolean(name: String, value: Boolean) {
        variables[name] = VariableValue.BooleanValue(value)
    }
    
    fun remove(name: String) {
        variables.remove(name)
    }
    
    fun getValue(name: String): VariableValue? = variables[name]

    fun getAsString(name: String): String? = when (val value = variables[name]) {
        is VariableValue.StringValue -> value.value
        is VariableValue.NumberValue -> value.value.toString()
        is VariableValue.BooleanValue -> value.value.toString()
        null -> null
    }
}
