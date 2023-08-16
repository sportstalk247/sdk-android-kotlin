
[![Release](https://jitpack.io/v/io.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#io.github.sportstalk247/sdk-android-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sportstalk247/sdk-coroutine-android?label=Maven%20Central)](https://search.maven.org/artifact/io.github.sportstalk247/sdk-coroutine-android)

# sdk-android-kotlin

# Implementing the SDK

You can download the latest SportsTalk Android SDK from the following location:

https://github.com/sportstalk247/sdk-android-kotlin

You need to register SportsTalk API with 'Appkey' and 'Token'.
How to get API Key and Token
You need to visit the dashboard with the following URL:

https://dashboard.sportstalk247.com

Then click on ''Application Management'' link to generate the above

# How to download the SDK from public repository

The SportsTalk SDK has been published into **jitpack.io**.

In order to use it in your application, do the following:

1. Add the following in root **build.gradle** file

For Groovy:
```groovy
allprojects {
    repositories {
    // ...
       mavenCentral()   // Make sure that Maven Central is declared
       maven {
          url "https://jitpack.io"
       }
    }
}
```
For Kotlin DSL:
```kotlin
allprojects {
    repositories { 
       // ...
       mavenCentral()   // Make sure that Maven Central is declared
       maven("https://jitpack.io")
       // ...
    }
}
```

2. Add the following lines in your module **build.gradle** file, depending on the chosen SDK implementation(Coroutine or Rx2Java), under dependencies section:

For Groovy:
```groovy
// For SDK coroutine implementation
implementation 'io.github.sportstalk247:sdk-android-kotlin:sdk-coroutine:X.Y.Z'
// OR
// For SDK Rx2Java implementation
implementation 'io.github.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z'
```
For Kotlin DSL:
```groovy
// For SDK coroutine implementation
implementation("io.github.sportstalk247:sdk-android-kotlin:sdk-coroutine:X.Y.Z")
// OR
// For SDK Rx2Java implementation
implementation("io.github.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z")
```

[![Release](https://jitpack.io/v/io.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#io.github.sportstalk247/sdk-android-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sportstalk247/sdk-coroutine-android?label=Maven%20Central)](https://search.maven.org/artifact/io.github.sportstalk247/sdk-coroutine-android)

Then sync again. The gradle build should now be successful.

# Migration Guide

Changed repository from [Gitlab](https://gitlab.com/sportstalk247/sdk-android-kotlin) to [Github](https://github.com/sportstalk247/sdk-android-kotlin), hence, dependency module identifier changed:
From:
```kotlin
/*implementation("com.gitlab.sportstalk247:sdk-android-kotlin:sdk-coroutine:X.Y.Z")*/
/*implementation("com.gitlab.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z")*/
```
To:
```kotlin
implementation("io.github.sportstalk247:sdk-android-kotlin:sdk-coroutine:X.Y.Z")
implementation("io.github.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z")
```

There are significant dependency updates made on version `1.3.0`. Notable changes are as follows:
* Using Kotlin version (1.9.0)[https://github.com/JetBrains/kotlin/releases/tag/v1.9.0]
* Built on Gradle version (8.2.1)[https://docs.gradle.org/8.2.1/release-notes.html]
* Build on Android Gradle Plugin version (8.1.0)[https://docs.gradle.org/8.2.1/release-notes.html]
* Using Kotlin Coroutines (1.7.3)[https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.7.3]
 
Therefore, it is recommended that the client apps should be using:
* Kotlin version of at least `1.8.10` and above
* Built on Gradle version of at least `7.5.1` and above
* Built on Android Gradle Plugin version of at least `7.4.2` and above

# Modules

* [sdk-coroutine](sdk-coroutine/README.md) - Provides [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) implementation of the SDK, using suspend functions.
   * The project implementing this artifact must at least have the following dependencies:
   ```groovy
   implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.10"  // also compatible with `1.9.0`
   ```

* [reactive-rx2](sdk-reactive-rx2/README.md) - Provides [RxJava 2.x](https://github.com/ReactiveX/RxJava/tree/2.x) implementation of the SDK, using RxJava return types.
   * The project implementing this artifact must at least have the following dependencies:
   ```groovy
   implementation "io.reactivex.rxjava2:rxjava:2.2.0"
   ```

# Documentation

Full Android SDK documentation found here: https://sdk-android-kotlin.readthedocs.io/en/latest/