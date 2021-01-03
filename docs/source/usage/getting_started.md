# Getting Started

Use this API to create experiences powered by SportsTalk and interact with those experiences.

* All API calls should be made using HTTPS.
* The API is designed to minimize the number of requests you need to make so chat applications are able to serve users very quickly especially for mobile users who need the lowest possible latency

## Implementing the SDK

You can download the latest SportsTalk Android SDK from the following location:

<https://gitlab.com/sportstalk247/sdk-android-kotlin>

You need to register SportsTalk API with 'Appkey' and 'Token'.

## How to get API Key and Token

You need to visit the dashboard with the following URL:

<https://dashboard.sportstalk247.com>

Then click on "Application Management" link to generate the above

## Authentication

* All requests that require authentication must have the x-api-token header with your application token.
* Most requests require authentication.
* If you provide authentication on a request that does not require authentication the header will be ignored and it will have no effect.

## SDK flow for Chat Applications

The typical flow of an application is:

* *Create Room*: Creates a chat room.
* *List Rooms*: Returns a list of available rooms. If you know the ID of the room you want there is no need to invoke List All Rooms first.
* *Join Room*: A user joins the room as an anonymous user or as a logged in user. Only logged in users can engage in chat activities. Anonymous users can only view whats happening in the room. You can also use custom room IDs that you provide so that you can use naming conventions to and join rooms without needing to call List All Rooms to get a room ID first.
* *Get Updates*: This gets the most recent events that have occurred in a room. You can use this endpoint as often as you want for polling, or you can use the Firebase API to get more bandwidth efficent push events when updates occur in the room. Calling Get Updates will prevent a logged in user from being removed from the room due to inactivity.
* *Execute Chat Command*: This performs a command in a chat room, like when you run a program from the command line. By default, the command is to say something in the room. However if a command starts with a special character such as / you can perform an action. See the Execute Chat Command API for more details and possible commands.
* *Exit Room*: When a logged in user exits the room, call this event. Otherwise the user will be removed from the room after some time without any activity.
* *List Participants*: Lists the logged in users in the chat room.
* *Get Room Details*: Gets statistics about the room such as the number of participants.
