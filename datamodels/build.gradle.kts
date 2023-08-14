@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
    signing
}

android {
    /*buildToolsVersion = rootProject.ext.buildToolsVersion*/
    namespace = "com.sportstalk.datamodels"
    compileSdk = libs.versions.compileSdk.get().toInt(10)

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt(10)
        targetSdk = libs.versions.targetSdk.get().toInt(10)

        aarMetadata {
            minCompileSdk = libs.versions.minSdk.get().toInt(10)
        }

        testFixtures {
            enable = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            //
            // By default, the 'debug' build type enables debugging options
            // and signs your APK with a generic debug keystore.
            //
            /*isDebuggable = true*/
            isMinifyEnabled = false
        }

        release {
            //
            // The 'release' build type strips out debug symbols
            // and requires you to create a release key and keystore for your app.
            //
            /*isDebuggable = false*/
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        apiVersion = "1.9"
        languageVersion = "1.9"

        jvmTarget = "17"

        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.serialization.UnstableDefault",
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes.add("META-INF/main.kotlin_module")
    }

    /**
     * https://developer.android.com/build/publish-library/configure-pub-variants#multiple-pub-vars
     */
    publishing {
        multipleVariants {
            includeBuildTypeValues("debug", "release")
            /*allVariants()*/
            /*withJavadocJar()*/
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    testImplementation(libs.robolectric)

    // Jetbrains Kotlin Dependencies
    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.serialization.json)

    // Dependencies for local unit tests
    testImplementation(libs.bundles.unitTest)   // exclude group: 'org.mockito'

}

configurations {
    all {
        resolutionStrategy{
            cacheChangingModulesFor(0, "seconds")
        }
    }
}

publishing {
    publications {
        listOf(
            "debug",
            "release",
        ).forEach { name ->
            register<MavenPublication>(name) {
                afterEvaluate {
                    from(components[name])
                }
            }
        }
    }
}