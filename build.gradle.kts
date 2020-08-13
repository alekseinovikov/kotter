buildscript {
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:${project.extra["protobuf_gradle_version"]}")
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
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

        testImplementation(kotlin("test-junit5"))
        testImplementation("org.junit.jupiter:junit-jupiter:${project.extra["junit_jupiter_version"]}")
    }

    tasks.withType<Test> {
        useJUnitPlatform { }
    }

}

