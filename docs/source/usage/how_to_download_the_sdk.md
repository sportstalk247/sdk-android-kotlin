# How to download the SDK

## Download from Repository

The SportsTalk SDK has been published into [Maven Central](https://central.sonatype.com/).
In order to use it in your application, just do the following:

1. Add the following in root  **build.gradle** file

``` tabs::

    .. code-tab:: groovy build.gradle(root)
        
        // ...
        allprojects {
            repositories {
               // ...
               mavenCentral()
               maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
               maven {
                  url "https://jitpack.io"
               }
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
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                maven("https://jitpack.io")
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
            implementation 'io.github.sportstalk247.sdk-android-kotlin:sdk-coroutine:X.Y.Z'
            // OR
            // For SDK Rx2Java implementation
            implementation 'io.github.sportstalk247.sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z'
        }
        // ...

    .. code-tab:: kotlin build.gradle.kts(per module)
        
        // ...
        dependencies {
            // For SDK coroutine implementation
            implementation("io.github.sportstalk247.sdk-android-kotlin:sdk-coroutine:X.Y.Z")
            // OR
            // For SDK Rx2Java implementation
            implementation("io.github.sportstalk247.sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z")
        }
        // ...
        
```

![Jitpack](https://img.shields.io/jitpack/version/io.github.sportstalk247/sdk-android-kotlin?label=Jitpack%20%7C%20sdk-coroutine&color=4DC621)
![Jitpack](https://img.shields.io/jitpack/version/io.github.sportstalk247/sdk-android-kotlin?label=Jitpack%20%7C%20sdk-reactive-rx2&color=4DC621)


![Maven Central](https://img.shields.io/maven-central/v/io.github.sportstalk247.sdk-android-kotlin/sdk-coroutine?label=Maven%20Central%20%7C%20sdk-coroutine)
![Maven Central](https://img.shields.io/maven-central/v/io.github.sportstalk247.sdk-android-kotlin/sdk-reactive-rx2?label=Maven%20Central%20%7C%20sdk-reactive-rx2)

Then sync again. The gradle build should now be successful.
