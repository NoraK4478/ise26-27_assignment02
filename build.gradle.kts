import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "2.4.21"
    application
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("dev.detekt") version "2.0.0-alpha.6"
}

group = "de.seuhd"
version = "0.0.1"

// https://mvnrepository.com/artifact/org.junit/junit-bom
val junitVersion = "6.1.3"

kotlin {
    jvmToolchain(25)
}

// Formatting and style follow the official Kotlin style; the rules and the line length come from .editorconfig.
// The plugin wires ktlintCheck into `check`, so a ktlint finding fails `gradle build`.
ktlint {
    version = "1.8.0"
}

// ktlint 1.8.0 depends on a Kotlin 2.2 compiler, which calls sun.misc.Unsafe methods that JDK 25 warns about on
// every build (JEP 498). ktlint runs on the Kotlin 2.4.10 compiler without these warnings, whereas the compiler of
// this build (2.4.21) fails in ktlint with "Extensions storage is not registered". Remove this block once a ktlint
// release ships with a Kotlin 2.4 compiler.
configurations.named("ktlint") {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("2.4.10")
        }
    }
}

// Use detekt's default rules, with the overrides in config/detekt/detekt.yml.
// The plugin wires its task into `check` and fails the build on findings.
detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("config/detekt/detekt.yml"))
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
    // Gradle needs the JUnit Platform launcher on the test runtime classpath.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    // Kotlin compiles the top-level main function in Main.kt into the class MainKt.
    mainClass = "de.seuhd.teaching.list.MainKt"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        // List every test in the console output, not only the failing ones.
        events("passed", "skipped", "failed")
        // Show the full assertion message (expected and actual value) for failing tests.
        exceptionFormat = TestExceptionFormat.FULL
    }
}
