# sdk-android-kotlin

New repo

# How to use

 You can download the latest SportsTalk Android SDK from the following location:
 https://gitlab.com/sportstalk247/sdk-android-kotlin

 You need to register SportsTalk API with 'Appkey' and 'Token'.


 How to get API Key and Token

 You need to visit the dashboard with the following URL:
 https://dashboard.sportstalk247.com

 Then click on ''Application Management'' link to generate the above

 # How to download the SDK from public repository

 The SportsTalk SDK has been published into **jitpack.io**.

 In order to use it in your application, just do the following:

 1. Add the following in root  **build.gradle** file
 ```
 allprojects {
     repositories {
     ...
     maven {
             url "https://jitpack.io"
     }
   }
 }
 ```

 2. Add the following lines in your module **build.gradle** file, under dependencies section

 ```
 implementation 'com.gitlab.sportstalk247:sdk-android-kotlin:master-SNAPSHOT'
 ```

 3. Add the following entries into the **AndroidManifest.xml** file, within `<application/>` tag:

 ```
 <meta-data
     android:name="sportstalk.api.auth_token"
     android:value="{YOU_API_AUTH_TOKEN}"/>

 <meta-data
     android:name="sportstalk.api.app_id"
     android:value="{YOU_API_APP_ID}"/>
 ```

 Then sync again. That is all.
 
 # How to Use
 ## Instantiate SportsTalkManager
 Within your  custom`Application` class, under `onCreate()`, include the following code snippet:
 ```
 SportsTalkManager.init(applicationContext)
 // Alternatively, developers can explicitly indicate their AppID/AuthToken/URLEndpoint on init()
 // SportsTalkManager.init(applicationContext, "c84cb9c852932a6b0411e75e", "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", "http://127.0.0.1")

 ```
 This will instantiate the singleton instance of the SDK.