package dev.stromland.jsonschema.module.kotlin.resolvers

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class TypeMetadataTest {

    @Test
    fun `verify which properties are default or not`() {
        data class Car(val brand: String, val electric: Boolean = true)

        val register = TypeMetadataRegister()
        val metadata = register.resolveType(Car::class.java)

        assertThat(metadata).isNotNull().let { assert ->
            assert.given {
                assertThat(it.hasPropertyADefaultValue(Car::brand)).isFalse()
                assertThat(it.hasPropertyADefaultValue(Car::electric)).isTrue()
            }
        }
    }
}

