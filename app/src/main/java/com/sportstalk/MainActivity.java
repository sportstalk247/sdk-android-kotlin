package com.sportstalk;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getName();

    private SportsTalkClient sportsTalkClient;

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
                Log.d(TAG, " onPoll start ...");
            }

            @Override
            public void onReaction(Event event) {
                Log.d(TAG, " onReaction start ..." + event.getId());
                // send reaction
               // CommandOptions commandOptions = new CommandOptions();
               // commandOptions.setReplyTo("");
               // sportsTalkClient.sendReaction("hello", Reaction.like, event.getId(), commandOptions );
            }

            @Override
            public void onAdminCommand(Event event) {
                Log.d(TAG, " onAdmin start ...");
            }

            @Override
            public void onPurge(Event event) {
                Log.d(TAG, " onPurge start ...");
            }

            @Override
            public void onSpeech(Event event) {
                Log.d(TAG, "Onspeech start....");
            }

            @Override
            public void onChat(Event event) {
                Log.d(TAG, "Onchat started.....");
            }
        };

        APICallback apiCallback = new APICallback() {
            @Override
            public void execute(ApiResult<JSONObject> apiResult, String action) {
                if ("listRooms".equals(action)) {
                    try {
                        Log.d(TAG, " list rooms callback ..." + apiResult.getData().getJSONObject("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("joinRoom".equals(action)) {
                    Log.d(TAG, " join room callback ..." + apiResult.getData());
                } else if ("sendCommand".equals(action)) {
                    Log.d(TAG, " command callback ..." + apiResult.getData());
                } else if ("user".equals(action)) {
                    Log.d(TAG, " user callback ..." + apiResult.getData());
                } else if ("listUserMessages".equals(action)) {
                    Log.d(TAG, " list user messages callback ..." + apiResult.getData());
                }
            }

            @Override
            public void error(ApiResult<JSONObject> apiResult, String action) {
            }
        };

        sportsTalkConfig.setEventHandler(eventHandler);
        sportsTalkConfig.setApiCallback(apiCallback);

        User user = new User();
        user.setUserId("001864a867604101b29672e904da688a");
        user.setDisplayName("Aldo");
        sportsTalkConfig.setUser(user);

        final SportsTalkClient sportsTalkClient = new SportsTalkClient(sportsTalkConfig);

        ///// start the operations
        sportsTalkClient.createOrUpdateUser();
        addDelay();
        Map<String, String> data = new HashMap<>();
        data.put("userId",      user.getUserId());
        data.put("handle",      user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("puctureurl",  user.getPictureUrl());
        data.put("profileurl",  user.getProfileUrl());

        sportsTalkClient.joinRoom("5dd9d5a038a28326ccfe5743", data);
        addDelay();
        sportsTalkClient.sendCommand("hello", null, "5dd9d5a038a28326ccfe5743");
        addDelay();
        sportsTalkClient.listUserMessages(100, "");
        addDelay();
        CommandOptions commandOptions = new CommandOptions();


        setContentView(R.layout.activity_main);
    }

    private void addDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
