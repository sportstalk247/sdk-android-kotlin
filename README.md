
 # How to use
 
 You can download the latest SportsTalk Android SDK from the following location:
 https://gitlab.com/sportstalk247/sdk-android.git 
 
 You need to register SportsTalk API with 'Appkey' and 'Token'. 
 
 
 How to get API Key and Token
 
 You need to visit the dashboard with the following URL:
 https://dashboard.sportstalk247.com
 
 Then click on ''Application Management'' link to generate the above
 
 # SportsTalk Chat SDK
 
 This SDK contains the following modules:
 
 ```
  - users
  - Chats ( Room and Moderation)
  - Comments(Conversation and Moderation)  
 ```
 
 These are the important modules.
 
 # Using the SDK
 
 SportsTalk API is having implementation for REST end points which will give you the information about
 users, rooms, chat messages and comments etc. 
 
 This SDK is designed based on Even driven architecture where the events or information are retrieved from
 the server by using polling mechanism.
 
 ```
 // create a SportsTalk client
 
 SportsTalkConfig sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("api key");
        sportsTalkConfig.setContext(MainActivity.this.getApplicationContext());
        sportsTalkConfig.setAppId("you app id");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");
        sportsTalkConfig.setUser(user);
        
 ChatClient chatClient = ChatClient.create(sportsTalkConfig);
 
// Create a Room
RoomResult room = new RoomResult();
room.setName("test-room");
room.setSlug("test-slug");

//create a room
RoomResult roomResult = chatClient.createRoom(room); 

// join room
RoomUserResult roomUserResult = chatClient.joinRoom(roomResult);

// register for updates
chatClient.startTalk();

// send command
CommandOptions options = new CommandOptions();
chatClient.sendCommand(options, "my first comment"); 
```

# Register Event

Using EventHandler you can register the upates

```
eventHandler = new EventHandler() {
            @Override
            public void onEventStart(Event event) {
            }
            
            @Override
            public void onChatStart(Event event) {
            }
 
            ...
};
sportsTalkConfig.setEventHandler(eventHandler);
```

The Android SDK demo can be downloaded from : (https://gitlab.com/sportstalk247/android-demo)
You can check the web demo using JavaSCript SDK : (https://www.sportstalk247.com/demo.html)
            
# Events

**Chat event**
This is event is used to get the chat messages sent by other users
```
public void onChat(final Event event)
```

** Goal event **
The goal event is used to get the goal image sent by the other users
```
public void onGoalCommand(EventResult eventResult)
```

** Purge event **
This event is fired when the messages are deleted or purged
```
public void onPurge(Event event)
```

** Reply event **
This event is fired when a reply to a chat message
```
public void onReply(final Event event)
```

** Reaction event **
This event is fired when a user is recting to a message. (LIKE, VOTE etc)
```
public void onReaction(Event event)
```

** Admin Command  event **
These event is fired when an admin user sends some message to the user. For example
if the admin is executing a purge command, then this event will be fired. 
```
public void onAdminCommand(Event event)
```

# Points to Consider

1. It is mandatory to join a room first before getting any events

2. In order to get these events, it is required to call **startTalk()** 

# How to download the SDK from public repository

The SpprtsTalk SDK has been published into **jitpack.io**.

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

2. Add the following lines in your module build.gradle file

```
implementation 'com.gitlab.sportstalk247:sdk-android:master'
```

3. Add the following token into the **gradle.properties** file

```
authToken=jp_5r3bbdtodp10jmr1qde9hkc27o
```

Then sync again. That is all.

The SDK has been imported into your project.