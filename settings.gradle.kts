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

rootProject.name = "ise26-27_assignment02"
