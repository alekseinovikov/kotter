import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

apply(plugin = "com.google.protobuf")

dependencies {
    implementation(project(":core"))
    implementation(project(":file-engine-api"))

    implementation("com.google.protobuf:protobuf-java:${project.extra["google_protobuf_version"]}")
}

val genProtoDir = "$projectDir/gen"

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${extra["google_protobuf_version"]}"
    }

    generatedFilesBaseDir = genProtoDir
}

tasks.withType<Delete> {
    delete(genProtoDir)
}

sourceSets {
    main {
        java {
            srcDirs("${genProtoDir}/main/java")
        }
    }
}
