
 Import into Android Studio
 ==========================
 Import the project into Android studio and change the following line in 'build.gradle' file.
  
  Comment the following line:
  
  apply plugin: 'com.android.library'
  
  and uncomment the next line
  apply plugin: 'com.android.application'
  
  Next, uncomment the application id so that you can create an apk file and test it in the emulator.
  
 
 How to build
 ============
 
 Import this application into the Android Studio and change the following line in 'build.gradle' file.
 
 apply plugin: 'com.android.application'
 
 Change the above line to as follows:
 
 apply plugin: 'com.android.library'
 
 Then 'sync project with Gradle file'
 
 Now this becomes an Android library (with .aar file)
 
 Then commit these changes to the repository
 
 Once you take the build, it will generate an .aar file instead of .apk file.
 
 How to distribute
 =================
 
 
 How to extends this SDK
 =======================
 Currently sportstalk SDK makes use of Android Volley API to make the HTTP call. This is done with the help of HttpClient class.
 But in case if it is required to change the implementation, then only HttpClient needs to be modified. No other parts of the
 code needs to be changed for this.
 
 The data received from the HTTP operations is through a callback object passed to it. So if you want to change the HTTP operation with a new
 implementation, you just needs to use this callback object. 
 
 How to use Push Notification
 ============================
 If we enable push notification at the time of implementation, then push notification can be enabled.
 
 SportsTalkConfig config = new SportsTalkConfig();
 config.setPushEnabled(true);
 
 Polling
 ========
 The polling is started with the RestfulEventManager class. In order to avail the server side data, the SDK is using callback. The callback class is EventHandler.java and you need to register this callback in the implementation code.
 
 You can check 'MainActivity.java' for more information.
 
 