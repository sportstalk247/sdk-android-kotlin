# How to download the SDK

## Download from Repository

The SportsTalk SDK has been published into **jitpack.io**.
In order to use it in your application, just do the following:

1. Add the following in root  **build.gradle** file

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
implementation 'com.gitlab.sportstalk247:sdk-android-kotlin:sdk-coroutine:vX.Y.Z'
// OR
// For SDK Rx2Java implementation
implementation 'com.gitlab.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:vX.Y.Z'
```

[![Release](https://jitpack.io/v/com.gitlab.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.gitlab.sportstalk247/sdk-android-kotlin)

Then sync again. The gradle build should now be successful.
