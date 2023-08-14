pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://plugins.gradle.org/m2/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "sdk-android-kotlin"
include(":datamodels")
include(":sdk-coroutine")
include(":sdk-reactive-rx2")
