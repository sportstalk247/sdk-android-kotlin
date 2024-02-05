import java.io.FileInputStream
import java.util.Properties

val packageGroup by extra { "io.github.sportstalk247.${rootProject.name}" }
val packageVersion by extra { "1.3.2" }

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.dokka) apply false

    alias(libs.plugins.gradleNexusPublish)
    id("maven-publish")
    signing
}

/*tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}*/

val localProperties = Properties()
if(rootProject.file("local.properties").exists()) {
    localProperties.load(FileInputStream(rootProject.file("local.properties")))
}
allprojects {
    group = rootProject.extra["packageGroup"].toString()
    version = rootProject.extra["packageVersion"].toString()

    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {

        extensions.findByType<PublishingExtension>()?.apply {

            publications.withType<MavenPublication>().configureEach {

                artifact(emptyJavadocJar.get())

                pom {
                    name.set(rootProject.name)
                    description.set("A Kotlin Android library powered by SportsTalk.")
                    url.set("https://github.com/sportstalk247/sdk-android-kotlin")
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("dev-lcc")
                            name.set("Lawrence C. Cendaña")
                            /*email.set("<<Developer Email>>")*/
                        }
                    }
                    scm {
                        /*connection.set("<<SCM Connection URL>>")*/
                        /*developerConnection.set("<<SCM Dev Connection URL>>")*/
                        url.set("https://github.com/sportstalk247/sdk-android-kotlin")
                    }
                }
            }

        }

        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            val id = localProperties["signing.keyId"]?.toString()
            val key = localProperties["signing.key"]?.toString()
            val password = localProperties["signing.password"]?.toString()

            useInMemoryPgpKeys(id, key, password)
            sign(publishing.publications)
        }

        val signingTasks = tasks.withType<Sign>()
        signingTasks.configureEach {
            onlyIf { isReleaseBuild }
        }

        // Slack Thread: https://youtrack.jetbrains.com/issue/KT-46466
        // TODO: remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
        this.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
            dependsOn(signingTasks)
        }
    }
}

val isReleaseBuild: Boolean
    get() = localProperties.containsKey("signing.keyId")

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            username = localProperties["ossrhUsername"].toString()
            password = localProperties["ossrhPassword"].toString()
        }
    }
}
