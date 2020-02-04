package com.sportstalk.api;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sportstalk.SportsTalkConfig;
import com.sportstalk.User;
import com.sportstalk.Utils;
import com.sportstalk.rest.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class RestfulUserManager implements IUserManager {

    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulUserManager(SportsTalkConfig sportsTalkConfig){
        this.setConfig(sportsTalkConfig);
    }

    private void setConfig(SportsTalkConfig sportsTalkConfig){
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void listUserMessages(User user, String cursor, int limit) {
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/user/"+user.getUserId()+"/?limit=" + limit + "&cursor=" + cursor, apiHeaders, null, sportsTalkConfig.getEventHandler());
        httpClient.setAction("listUserMessages");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setBanStatus(User user, boolean isBanned) {
        Map<String, String> data = new HashMap<>();
        data.put("banned", Boolean.toString(isBanned));
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/user/" + user.getUserId() + "/ban", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("banStatus");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void createOrUpdateUser(User user) {
        Map<String, String> data = new HashMap<>();
        data.put("userid",      user.getUserId());
        data.put("handle",      user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("profileurl",  user.getPictureUrl());
        data.put("pictureurl",  user.getPictureUrl());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/user/" + user.getUserId(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("user");
        httpClient.execute();
    }

}
