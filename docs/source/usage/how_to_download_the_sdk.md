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
            implementation 'com.github.sportstalk247.sdk-android-kotlin:sdk-coroutine:X.Y.Z'
            // OR
            // For SDK Rx2Java implementation
            implementation 'com.github.sportstalk247.sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z'
        }
        // ...

    .. code-tab:: kotlin build.gradle.kts(per module)
        
        // ...
        dependencies {
            // For SDK coroutine implementation
            implementation("com.github.sportstalk247.sdk-android-kotlin:sdk-coroutine:X.Y.Z")
            // OR
            // For SDK Rx2Java implementation
            implementation("com.github.sportstalk247.sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z")
        }
        // ...
        
```

[![Release](https://jitpack.io/v/com.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.github.sportstalk247/sdk-android-kotlin)

Then sync again. The gradle build should now be successful.
