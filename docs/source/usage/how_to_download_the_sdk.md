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

2. Add the following lines in your module **build.gradle** file, under dependencies section

```groovy
implementation 'com.gitlab.sportstalk247:sdk-android-kotlin:vX.Y.Z'
```

[![Release](https://jitpack.io/v/com.gitlab.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.gitlab.sportstalk247/sdk-android-kotlin)

Then sync again. The gradle build should now be successful.