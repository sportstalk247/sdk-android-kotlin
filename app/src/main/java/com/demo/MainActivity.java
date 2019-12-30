package com.demo;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.binu.myapplication.R;
import com.sportstalk247.APICallback;
import com.sportstalk247.ApiResult;
import com.sportstalk247.Event;
import com.sportstalk247.PollEventHandler;
import com.sportstalk247.SportsTalkClient;
import com.sportstalk247.SportsTalkConfig;
import com.sportstalk247.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// integrate Sporttalk client
        SportsTalkConfig sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("vfZSpHsWrkun7Yd_fUJcWAHrNjx6VRpEqMCEP3LJV9Tg");
        sportsTalkConfig.setContext(MainActivity.this.getApplicationContext());
        PollEventHandler pollEventHandler = new PollEventHandler() {
            @Override
            public void onPollStart(Event event) {
                System.out.println(" onPoll start ...");
            }

            @Override
            public void onReaction(Event event) {
                System.out.println(" onReaction start ...");
            }

            @Override
            public void onAdminCommand(Event event) {
                System.out.println(" onAdmin start ...");
            }

            @Override
            public void onPurge(Event event) {
                System.out.println(" onPurge start ...");
            }
        };
        sportsTalkConfig.setPollEventHandler(pollEventHandler);
        User user = new User();
        user.setUserId("001864a867604101b29672e904da688a");
        user.setDisplayName("Aldo");
       // user.setHandle("unni");
        sportsTalkConfig.setUser(user);
        final SportsTalkClient sportsTalkClient = new SportsTalkClient(sportsTalkConfig);

        APICallback apiCallback = new APICallback() {
            @Override
            public void execute(ApiResult<JSONObject> apiResult, String action) {
                if("listRooms".equals(action)) {
                    try {
                        System.out.println("success -> " + apiResult.getData().getJSONObject("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if("joinRoom".equals(action)) {
                    System.out.println(" join room callbck .....");
                    System.out.println(apiResult.getData());
               //     sportsTalkClient.startPollUpdate();
                }else if("startPoll".equals(action)) {
                    System.out.println(" start poll callbck .....");
                }else if("command".equals(action)) {
                    System.out.println(" command callbck .....");
                    System.out.println(apiResult.getData());
                }else if("user".equals(action)) {
                    System.out.println(" user callbck .....");
                    System.out.println(apiResult.getData());
                }
            }

            @Override
            public void error(ApiResult<JSONObject> apiResult, String action) {
            }
        };


        //sportsTalkClient.listRooms(data, apiCallback);
        sportsTalkClient.createOrUpdateUser(apiCallback);
        addDelay();
        Map<String, String> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("handle", user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("puctureurl", user.getPictureUrl());
        data.put("profileurl", user.getProfileUrl());

        sportsTalkClient.joinRoom(apiCallback, "5dd9d5a038a28326ccfe5743", data);
        /// register poll update
       //   sportsTalkClient.startPollUpdate();
        addDelay();
        sportsTalkClient.sendCommand("hello", null, "5dd9d5a038a28326ccfe5743", apiCallback);
        setContentView(R.layout.activity_main);
    }

    private void addDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void startPolling() {
    }

}
