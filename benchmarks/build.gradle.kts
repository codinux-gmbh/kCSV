import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    id("me.champeau.jmh") version "0.7.2"
}

repositories {
    mavenCentral()
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


val jmhDepVersion: String by project

dependencies {
    implementation(rootProject)
    jmh("de.siegmar:fastcsv:3.0.0")

    jmh("org.openjdk.jmh:jmh-kotlin-benchmark-archetype:$jmhDepVersion")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:$jmhDepVersion")

    // this is the line that solves the missing /META-INF/BenchmarkList error
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$jmhDepVersion")
}

jmh {
    jmhVersion.set(jmhDepVersion)

    warmupIterations.set(2)
    iterations.set(2)
    fork.set(2)
    operationsPerInvocation.set(1)

    val dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now())
//    humanOutputFile.set(project.file("${project.buildDir}/reports/jmh/human_$dateTime.txt")) // human-readable output file
    resultsFile.set(project.file("${project.buildDir}/reports/jmh/results_$dateTime.txt"))
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("org.openjdk.jmh.annotations.BenchmarkMode")
    annotation("org.openjdk.jmh.annotations.Fork")
    annotation("org.openjdk.jmh.annotations.Warmup")
    annotation("org.openjdk.jmh.annotations.Measurement")
}