import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.61"
    id("org.jetbrains.dokka") version "0.10.1"
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.0")
    implementation("com.github.victools:jsonschema-module-jackson:4.0.0")
    implementation("com.github.victools:jsonschema-generator:4.3.0")
    runtimeOnly("org.slf4j:slf4j-simple:1.6.2")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.21")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
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
    from(tasks.dokka)
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
            url = uri("/home/espen/.m2/repository")
        }
    }
}
