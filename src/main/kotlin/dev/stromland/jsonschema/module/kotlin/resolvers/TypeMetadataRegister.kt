package dev.stromland.jsonschema.module.kotlin.resolvers

import java.lang.reflect.Type
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

/**
 * Cache for creating an instance of a class with required parameters.
 * @property typeToMetadataMap map
 */
data class TypeMetadataRegister(private val typeToMetadataMap: Map<Type, TypeMetadata> = mapOf()) {
    private val defaultTypeValues = mutableMapOf(
        Int::class.java.toTypedPair(0),
        Boolean::class.java.toTypedPair(false),
        String::class.java.toTypedPair(""),
        Long::class.java.toTypedPair(0L),
        Map::class.java.toTypedPair(mapOf<Any, Any>()),
        List::class.java.toTypedPair(listOf<Any>())
    )

    init {
        defaultTypeValues.putAll(typeToMetadataMap)
    }

    fun resolveType(type: Type): TypeMetadata? {
        val alreadyResolved = getTypeMetadata(type)
        if (alreadyResolved != null) {
            return alreadyResolved
        }
        return registerType(type)
    }

    fun getDefaultValue(type: Type, prop: KProperty<*>? = null): Any? {
        val metadata = defaultTypeValues[type] ?: registerType(type) ?: return null
        return metadata.getDefaultValue(prop)
    }

    private fun registerType(clazz: Type): TypeMetadata? {
        fun getDefaultRequiredValue(param: KParameter): Any? {
            val typeMetadata = defaultTypeValues[param.type.javaType]
            if (typeMetadata != null) {
                return typeMetadata.instance
            }
            return registerType(param.type.javaType)?.instance
        }

        if (clazz !is Class<*>) {
            return null
        }

        if (clazz.isEnum) {
            val value = Class.forName(clazz.name).enumConstants.firstOrNull() ?: return null
            return clazz.toTypeMetadata(value, mapOf())
        }

        val constructor = clazz.kotlin.primaryConstructor ?: return null

        val requiredParameters: Map<KParameter, Any?> = constructor.parameters
            .filter { !it.isOptional }
            .associateWith { getDefaultRequiredValue(it) }

        val instant = constructor.callBy(requiredParameters)
        val metadata = clazz.toTypeMetadata(instant, requiredParameters)
        defaultTypeValues[clazz] = metadata
        return metadata
    }

    private fun getTypeMetadata(type: Type): TypeMetadata? {
        return defaultTypeValues[type]
    }
}