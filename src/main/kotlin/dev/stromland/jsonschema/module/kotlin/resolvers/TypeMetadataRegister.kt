package dev.stromland.jsonschema.module.kotlin.resolvers

import java.lang.reflect.Type
import java.time.Instant
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

/**
 * Register for creating an instance of a class with required parameters. And a cache for resolved types.
 * @param typeToMetadataMap additional types.
 */
data class TypeMetadataRegister(private val typeToMetadataMap: Map<Type, TypeMetadata> = mapOf()) {
    private val defaultTypeValues = mutableMapOf(
        Int::class.java.toTypedPair(0),
        Integer::class.java.toTypedPair(0),
        Boolean::class.java.toTypedPair(false),
        String::class.java.toTypedPair(""),
        Long::class.java.toTypedPair(0L),
        Map::class.java.toTypedPair(mapOf<Any, Any>()),
        List::class.java.toTypedPair(listOf<Any>()),
        Instant::class.java.toTypedPair(Instant.EPOCH)
    )

    init {
        defaultTypeValues.putAll(typeToMetadataMap)
    }

    fun resolveType(type: Type): TypeMetadata? {
        return getTypeMetadata(type) ?: registerType(type)
    }

    fun getDefaultValue(type: Type, prop: KProperty<*>? = null): Any? {
        val metadata = resolveType(type) ?: return null
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