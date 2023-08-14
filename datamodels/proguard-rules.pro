# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn module-info

# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}


-keep,includedescriptorclasses class com.sportstalk.datamodels.**$$serializer { *; }
-keepclassmembers class com.sportstalk.datamodels.** {
    *** Companion;
}
-keepclasseswithmembers class com.sportstalk.datamodels.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-dontwarn com.sportstalk.datamodels.**
-keep class com.sportstalk.datamodels.** {*;}

######################################
## Unit Test
######################################
-dontwarn org.mockito.**
-dontwarn sun.reflect.**
-dontwarn android.test.**
-dontwarn org.junit.**