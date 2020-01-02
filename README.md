
 How to build
 ============
 
 Import this application into the Android Studio and change the following line in 'build.gradle' file.
 
 apply plugin: 'com.android.application'
 
 Change the above line to as follows:
 
 apply plugin: 'com.android.library'
 
 Then 'sync project with Gradle file'
 
 Now this becomes an Android library (with .aar file)
 
 Then commit these changes to the repository
 
 How to distribute
 =================
 We can use jitpack repository support to distribute it.
 
 At the time of implementation, we need to add the followig line:
 
 allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
   }
   
Add this dependencies at the time of using this module.   
   dependencies {
        implementation 'com.sportstalk:sportstalk247:1.0'
   }
 
The 'jitpack' repo will checkout the code from the github and create an aar file automatically based on the build.gradle file.
 