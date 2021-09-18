package dev.stromland.jsonschema.module.kotlin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.victools.jsonschema.generator.FieldScope
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.module.jackson.JacksonModule
import dev.stromland.jsonschema.module.kotlin.resolvers.TypeMetadataRegister
import kotlin.reflect.jvm.kotlinProperty

data class KotlinJacksonModuleConfig(
    val disableRequired: Boolean = false,
    val disableDefaultValue: Boolean = false,
    val disableNullable: Boolean = false
)

/**
 * Module for jsonschema-generator. Takes advantage of Kotlin data classes.
 * Given a data class this module can add the following information to json schema:
 *  - Default values.
 *  - Nullable type.
 *  - Required properties. Required when a field is not nullable or doesn't have a default value.
 *
 * @param typeMetadataRegister register for resolving types.
 */
class KotlinJacksonModule(
    val config: KotlinJacksonModuleConfig = KotlinJacksonModuleConfig(),
    val typeMetadataRegister: TypeMetadataRegister = TypeMetadataRegister()
) :
    JacksonModule() {
    private var mapper = jacksonObjectMapper()

    override fun applyToConfigBuilder(builder: SchemaGeneratorConfigBuilder) {
        super.applyToConfigBuilder(builder)
        mapper = builder.objectMapper
            .registerKotlinModule()
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)

        builder.forFields().also {
            if (!config.disableRequired) it.withRequiredCheck(this::resolveRequiredCheck)
            if (!config.disableDefaultValue) it.withDefaultResolver(this::resolveDefault)
            if (!config.disableNullable) it.withNullableCheck(this::resolveNullable)
        }
    }

    private fun resolveRequiredCheck(field: FieldScope): Boolean {
        return field.isRequired()
    }

    private fun resolveDefault(field: FieldScope): Any? {
        val kProperty = field.rawMember.kotlinProperty ?: return null
        return typeMetadataRegister.getDefaultValue(field.member.declaringType.erasedType, kProperty)
    }

    private fun resolveNullable(field: FieldScope): Boolean {
        val type = field.rawMember.kotlinProperty
        return type?.returnType?.isMarkedNullable ?: false
    }

    /**
     * EXTENSION FUNCTIONS
     */

    private fun FieldScope.getAnnotatedParameter(): AnnotatedParameter? {
        val constructor = this.getAnnotatedConstructor()

        val index = constructor?.annotated?.parameters?.indexOfFirst {
            it.name == this.name
        } ?: return null

        if (index == -1) {
            return null
        }

        return constructor.getParameter(index)
    }

    private fun FieldScope.getAnnotatedConstructor(): AnnotatedConstructor? {
        val bean = getBeanDescriptionForClass(this.member.declaringType)
        return bean.constructors.takeIf { it.size > 0 }?.first()
    }

    private fun FieldScope.isRequired(): Boolean {
        val annotatedParameter = this.getAnnotatedParameter() ?: return false
        return mapper.serializationConfig.annotationIntrospector.hasRequiredMarker(annotatedParameter)
    }
}