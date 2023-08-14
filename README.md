
[![Release](https://jitpack.io/v/com.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.github.sportstalk247/sdk-android-kotlin)

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

```groovy
allprojects {
    repositories {
    // ...
       maven {
          url "https://jitpack.io"
       }
    }
}
```

2. Add the following lines in your module **build.gradle** file, depending on the chosen SDK implementation(Coroutine or Rx2Java), under dependencies section:

```groovy
// For SDK coroutine implementation
implementation 'com.github.sportstalk247:sdk-android-kotlin:sdk-coroutine:X.Y.Z'
// OR
// For SDK Rx2Java implementation
implementation 'com.github.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z'
```

[![Release](https://jitpack.io/v/com.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.github.sportstalk247/sdk-android-kotlin)

Then sync again. The gradle build should now be successful.

# Modules

* [sdk-coroutine](sdk-coroutine/README.md) - Provides [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) implementation of the SDK, using suspend functions.
   * The project implementing this artifact must at least have the following dependencies:
   ```groovy
   implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.50"
   ```

* [reactive-rx2](sdk-reactive-rx2/README.md) - Provides [RxJava 2.x](https://github.com/ReactiveX/RxJava/tree/2.x) implementation of the SDK, using RxJava return types.
   * The project implementing this artifact must at least have the following dependencies:
   ```groovy
   implementation "io.reactivex.rxjava2:rxjava:2.2.0"
   ```

# Documentation

Full Android SDK documentation found here: https://sdk-android-kotlin.readthedocs.io/en/latest/