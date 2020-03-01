package dev.stromland.jsonschema.module.kotlin

import assertk.assertThat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import org.junit.jupiter.api.Test

class KotlinJacksonModuleTest {
    val generator: SchemaGenerator

    init {
        val builder = SchemaGeneratorConfigBuilder(jacksonObjectMapper(), OptionPreset.PLAIN_JSON)
        builder.with(KotlinJacksonModule())
        builder.with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
        generator = SchemaGenerator(builder.build())
    }

    @Test
    fun `json schema for Person should match snapshot`() {
        data class Person(val name: String, val lastName: String, val age: Int = 28, val sex: String?)

        val jsonSchema = generator.generateSchema(Person::class.java)

        assertThat(jsonSchema.toPrettyString()).matchSnapshot("JSON schema for Person")
    }

    @Test
    fun `json schema for Complex should match snapshot`() {
        val jsonSchema = generator.generateSchema(Complex::class.java)

        assertThat(jsonSchema.toPrettyString()).matchSnapshot("JSON schema for Complex")
    }
}

enum class ConfigType {
    DEVELOPMENT,
    PRODUCTION,
}

data class ComplexConfig(
    val type: ConfigType,
    val enabled: Boolean = true,
    val properties: Map<String, String> = mapOf()
)

data class Complex(
    val name: String,
    val configs: List<ComplexConfig> = listOf(ComplexConfig(ConfigType.DEVELOPMENT)),
    val version: String?
)

