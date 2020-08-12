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

        testImplementation(kotlin("test-junit5"))
    }

    tasks.withType<Test> {
        useJUnitPlatform { }
    }

}

