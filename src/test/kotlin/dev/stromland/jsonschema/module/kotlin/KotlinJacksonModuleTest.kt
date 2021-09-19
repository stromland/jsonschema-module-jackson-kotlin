package dev.stromland.jsonschema.module.kotlin

import assertk.assertThat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.victools.jsonschema.generator.*
import dev.stromland.jsonschema.module.kotlin.resolvers.TypeMetadataRegister
import dev.stromland.jsonschema.module.kotlin.resolvers.toTypedPair
import org.junit.jupiter.api.Test

class KotlinJacksonModuleTest {

    @Test
    fun `json schema for Person should match snapshot`() {
        data class Person(val name: String, val lastName: String, val age: Int = 28, val gender: String?)

        val generator = getGenerator()
        val jsonSchema = generator.generateSchema(Person::class.java)

        assertThat(jsonSchema.toPrettyString()).matchSnapshot("JSON schema for Person")
    }

    @Test
    fun `json schema for Complex should match snapshot`() {
        val typeMetadataRegister = TypeMetadataRegister(mapOf(
            HashMap::class.java.toTypedPair(hashMapOf<Any, Any>())
        ))

        val generator = getGenerator(typeMetadataRegister)
        val jsonSchema = generator.generateSchema(Complex::class.java)

        assertThat(jsonSchema.toPrettyString()).matchSnapshot("JSON schema for Complex")
    }

    private fun getGenerator(typeMetadataRegister: TypeMetadataRegister = TypeMetadataRegister()): SchemaGenerator {
        val builder =
            SchemaGeneratorConfigBuilder(jacksonObjectMapper(), SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
        builder.with(KotlinJacksonModule(typeMetadataRegister = typeMetadataRegister))
        builder.with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
        builder.with(Option.ENUM_KEYWORD_FOR_SINGLE_VALUES)
        return SchemaGenerator(builder.build())
    }
}

enum class ConfigType {
    DEVELOPMENT,
    PRODUCTION,
}

data class ComplexConfig(
    val type: ConfigType,
    val enabled: Boolean = true,
    val properties: Map<String, String> = mapOf(),
    val test: HashMap<String, String>
)

data class Complex(
    val name: String,
    val configs: List<ComplexConfig> = listOf(ComplexConfig(ConfigType.DEVELOPMENT, test = hashMapOf())),
    val version: String?
)

