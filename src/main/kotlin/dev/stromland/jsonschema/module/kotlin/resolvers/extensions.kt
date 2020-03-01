package dev.stromland.jsonschema.module.kotlin.resolvers

import java.lang.reflect.Type
import kotlin.reflect.KParameter

fun Type.toTypedPair(value: Any): Pair<Type, TypeMetadata> {
    return this to this.toTypeMetadata(value)
}

fun Type.toTypeMetadata(defaultValue: Any, requiredParameters: Map<KParameter, Any?> = mapOf()): TypeMetadata {
    return TypeMetadata(this, defaultValue, requiredParameters)
}
