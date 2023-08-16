# How to download the SDK

## Download from Repository

The SportsTalk SDK has been published into **jitpack.io**.
In order to use it in your application, just do the following:

1. Add the following in root  **build.gradle** file

``` tabs::

    .. code-tab:: groovy build.gradle(root)
        
        // ...
        allprojects {
            repositories {
               // ...
               mavenCentral()
               // ...
            }
        }
        // ...

    .. code-tab:: kotlin build.gradle.kts(root)
        
        // ...    
        dependencyResolutionManagement {
            repositories {
                // ...
                mavenCentral()
                // ...
            }
        }
        // ...
        
```

2. Add the following lines in your module **build.gradle** file, depending on the chosen SDK implementation(Coroutine or Rx2Java), under dependencies section:

``` tabs::

    .. code-tab:: groovy build.gradle(per module)
        
        // ...
        dependencies {
            // For SDK coroutine implementation
            implementation 'io.github.sportstalk247:sdk-coroutine-android:X.Y.Z'
            // OR
            // For SDK Rx2Java implementation
            implementation 'io.github.sportstalk247:sdk-reactive-rx2-android:X.Y.Z'
        }
        // ...

    .. code-tab:: kotlin build.gradle.kts(per module)
        
        // ...
        dependencies {
            // For SDK coroutine implementation
            implementation("io.github.sportstalk247:sdk-coroutine-android:X.Y.Z")
            // OR
            // For SDK Rx2Java implementation
            implementation("io.github.sportstalk247:sdk-reactive-rx2-android:X.Y.Z")
        }
        // ...
        
```

[![Release](https://jitpack.io/v/io.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#io.github.sportstalk247/sdk-android-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sportstalk247/sdk-coroutine-android?label=Maven%20Central)](https://search.maven.org/artifact/io.github.sportstalk247/sdk-coroutine-android)

Then sync again. The gradle build should now be successful.
