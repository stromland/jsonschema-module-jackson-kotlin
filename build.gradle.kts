import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.dokka") version "1.6.10"
    id("com.github.ben-manes.versions") version "0.42.0"
}

group = "dev.stromland"
version = "0.0.3-SNAPSHOT"
description = "A module for JSON schema generator that benefits from Kotlin reflection."
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("com.github.victools:jsonschema-module-jackson:4.24.1")
    implementation("com.github.victools:jsonschema-generator:4.24.1")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.36")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
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
            jvmTarget = "11"
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
            from(components["kotlin"])
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
            repositories {
                maven {
                    name = "GitHubPackages"
                    setUrl("https://maven.pkg.github.com/stromland/jsonschema-module-jackson-kotlin")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR")
                        password = System.getenv("GITHUB_TOKEN")
                    }
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
