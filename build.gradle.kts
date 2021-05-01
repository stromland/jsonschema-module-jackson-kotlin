import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
    id("com.github.ben-manes.versions") version "0.38.0"
}

group = "dev.stromland"
version = "0.0.1-SNAPSHOT"
description = "A module for JSON schema generator that benefits from Kotlin reflection."

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("com.github.victools:jsonschema-module-jackson:4.18.0")
    implementation("com.github.victools:jsonschema-generator:4.18.0")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.30")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn.add(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(dokkaJar)
            artifact(sourcesJar)

            pom {
                name.set("Kotlin JSON Schema Generator Module – jackson")
                url.set("https://github.com/stromland/jsonschema-module-jackson-kotlin")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("stromland")
                        name.set("Espen Strømland")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/stromland/jsonschema-module-jackson-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/stromland/jsonschema-module-jackson-kotlin.git")
                    url.set("https://github.com/stromland/jsonschema-module-jackson-kotlin")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/.m2/repository")
        }
    }
}
