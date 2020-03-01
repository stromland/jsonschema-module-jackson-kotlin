package dev.stromland.jsonschema.module.kotlin.resolvers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.math.BigInteger

class TypeMetadataRegisterTest {

    @Test
    fun `should fetch inital default values`() {
        val register = TypeMetadataRegister()
        val stringDefault = register.getDefaultValue(String::class.java)
        val intDefault = register.getDefaultValue(Int::class.java)
        val notFoundDefault = register.getDefaultValue(BigInteger::class.java)

        assertThat(stringDefault).isEqualTo("")
        assertThat(intDefault).isEqualTo(0)
        assertThat(notFoundDefault).isNull()
    }

    @Test
    fun `should get default value for class`() {
        data class Pet(val type: String, val alive: Boolean = true)

        val register = TypeMetadataRegister()
        val typeDefault = register.getDefaultValue(Pet::class.java, Pet::type)
        val aliveDefault = register.getDefaultValue(Pet::class.java, Pet::alive)

        assertThat(typeDefault).isNull()
        assertThat(aliveDefault).isEqualTo(true)
    }
}

