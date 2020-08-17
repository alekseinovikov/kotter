import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:${project.extra["protobuf_gradle_version"]}")
    }
}

plugins {
    kotlin("jvm") version "1.4.0"
}

repositories {
    mavenCentral()
}

subprojects {
    group = "org.kotter"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.slf4j:log4j-over-slf4j:${project.extra["slf4j_version"]}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.extra["coroutines_version"]}")

        testImplementation(kotlin("test-junit5"))
        testImplementation("org.junit.jupiter:junit-jupiter:${project.extra["junit_jupiter_version"]}")
        testImplementation("org.assertj:assertj-core:${project.extra["assertj_version"]}")
    }

    tasks.withType<Test> {
        useJUnitPlatform { }
    }

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

}
