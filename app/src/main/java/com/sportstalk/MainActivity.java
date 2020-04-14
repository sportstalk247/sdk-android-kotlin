package com.sportstalk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;


import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getName();

    private SportsTalkClient sportsTalkClient;

    private Room room;

    private boolean once = false;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// integrate Sports talk client
        SportsTalkConfig sportsTalkConfig       = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("vfZSpHsWrkun7Yd_fUJcWAHrNjx6VRpEqMCEP3LJV9Tg");
        sportsTalkConfig.setContext(MainActivity.this.getApplicationContext());

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void onEventStart(Event event) {
                System.out.println(" event start.. " + event);

            }

            @Override
            public void onReaction(Event event) {
            }

            @Override
            public void onAdminCommand(Event event) {
            }

            @Override
            public void onReply(Event event) {

            }

            @Override
            public void onPurge(Event event) {
                System.out.println(" purge.. " + event);
            }

            @Override
            public void onSpeech(Event event) {
                try {
                    JSONObject jsonObject = new JSONObject(event.getCustomPayload().toString());
                    if(jsonObject != null && jsonObject.has("img")) {
                        String img = jsonObject.getString("img");
                        System.out.println(" img " + img);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(" speech.. " + event.getCustomPayload());
            }

            @Override
            public void onChat(Event event) {

                //System.out.println(" chat.. " + event);
                if(!once) {
                    System.out.println(" sending reply ....");
                    CommandOptions commandOptions = new CommandOptions();
                    commandOptions.setReplyTo(event.getId());
                    Map<String, String> data = new HashMap<>();
                    //sportsTalkClient.sendCommand("hello", commandOptions, "5dd9d5a038a28326ccfe5743");
                    //sportsTalkClient.sendReply("testing reply", event.getId(),commandOptions, "5dd9d5a038a28326ccfe5743", data);
                    sendGoalCommand();
                    once = true;
                }

            }

            @Override
            public void onNetworkResponse(List<EventResult> list) {
                room = (Room) list.get(0).getCustomPayload();
                System.out.println(" room id " + room.getId());

                RoomResult result = new RoomResult();
                result.setId(room.getId());
                Map<String, String>data1 = new HashMap<>();
                sportsTalkClient.joinRoom(result, room.getId(), data1);

            }

            @Override
            public void onHelp(ApiResult apiResult) {

            }

            @Override
            public void onGoalCommand(EventResult event) {
                System.out.println(" *** on Goal command " + event.getBody());
            }
        };
        sportsTalkConfig.setEventHandler(eventHandler);

        User user = new User();
        user.setUserId("001864a867604101b29672e904da688a");
        user.setDisplayName("Aldo");
        sportsTalkConfig.setUser(user);

        sportsTalkClient = SportsTalkClient.create(sportsTalkConfig);
        Map<String, String> data = new HashMap<>();
        data.put("userId",      user.getUserId());
        data.put("handle",      user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("puctureurl",  user.getPictureUrl());
        data.put("profileurl",  user.getProfileUrl());
        //sportsTalkClient.createRoom();
        addDelay();
        addDelay();

        RoomResult roomResult = new RoomResult();
        roomResult.setId(room ==null ? "5dd9d5a038a28326ccfe5743" : room.getId());
        sportsTalkClient.joinRoom(roomResult,"5dd9d5a038a28326ccfe5743", data);
        addDelay();
        addDelay();


        setContentView(R.layout.activity_main);
    }

    private void addDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendGoalCommand() {
        GoalOptions goalOptions = new GoalOptions();
        goalOptions.setLink("");
        sportsTalkClient.sendGoal("GOAL", "https://res.cloudinary.com/sportstalk247/image/upload/v1575821595/goal_l6ho1d.jpg", goalOptions);
    }

}
