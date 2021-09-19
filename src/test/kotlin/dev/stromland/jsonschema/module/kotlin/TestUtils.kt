package dev.stromland.jsonschema.module.kotlin

import assertk.Assert
import assertk.assertions.isEqualTo
import org.slf4j.LoggerFactory
import java.io.File

fun Assert<String>.matchSnapshot(snapshotName: String, type: String = "json") {
    val snapshot = Snapshot().getOrCreateSnapshot(snapshotName, type) {
        var value: String? = null
        this.given {
            value = it
        }
        value
    }

    this.isEqualTo(snapshot)
}

data class Snapshot(val path: String = defaultPath) {
    private val logger = LoggerFactory.getLogger(Snapshot::class.java)

    companion object {
        const val defaultPath = "src/test/resources/__snapshot__"
    }

    init {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    fun getOrCreateSnapshot(name: String, type: String, getContent: () -> String?): String? {
        val file = File("$path/$name.$type")
        val created = file.createNewFile()
        val shouldUpdate = System.getenv("UPDATE_SNAPSHOT") == "true"
        return if (created || shouldUpdate) {
            getContent()?.also {
                file.writeText(it)
                logger.info("snapshot written (${file.name})")
            }
        } else {
            file.readText()
        }
    }
}