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
 implementation 'com.gitlab.sportstalk247:sdk-android-kotlin:master'
 ```

 3. Add the following token into the **local.properties** file

 ```
 sportstalk.api.auth_token=<Your API TOKEN>
 sportstalk.api.app_id=<Your API APP ID>
 ```

 Then sync again. That is all.