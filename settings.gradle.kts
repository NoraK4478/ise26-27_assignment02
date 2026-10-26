pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // detekt 2.0's Gradle plugin pulls in org.gradle.experimental:gradle-public-api, which is
        // published only to Gradle's own repository.
        maven {
            url = uri("https://repo.gradle.org/gradle/libs-releases")
            content { includeGroup("org.gradle.experimental") }
        }
    }
}

// gradle/gradle-daemon-jvm.properties requires JDK 25 for the Gradle daemon. Gradle does not detect JDKs
// installed by mise, so if Gradle is started with another JDK, for example by IntelliJ, this plugin lets
// Gradle download a JDK 25 instead of failing.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "ise26-27_assignment02"
