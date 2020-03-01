package dev.stromland.jsonschema.module.kotlin.resolvers

import java.lang.reflect.Type
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

/**
 * Model for storing information about a type.
 * @property type The Java type of the class.
 * @property instance An instance of the class.
 * @property requiredParameters A map of requiredParameters to instantiate an instance of the type.
 */
data class TypeMetadata(val type: Type, val instance: Any, val requiredParameters: Map<KParameter, Any?> = mapOf()) {
    private val parameterNames: Set<String> = requiredParameters.mapNotNull { it.key.name }.toSet()

    /**
     * Get default value for the given type or for a property of the given type.
     * @param prop something.
     * @return return null if property is required, else the instant.
     */
    fun getDefaultValue(prop: KProperty<*>? = null): Any? {
        if (prop == null) {
            return instance
        }
        return if (prop.isRequired()) null else prop.call(instance)
    }


    /**
     * Determine if a property has a default value or not.
     * @param prop The property for validation.
     * @return True if property has a default value, false if not.
     */
    fun hasPropertyADefaultValue(prop: KProperty<*>): Boolean {
        return !prop.isRequired()
    }

    private fun KProperty<*>.isRequired() = parameterNames.contains(this.name)
}

