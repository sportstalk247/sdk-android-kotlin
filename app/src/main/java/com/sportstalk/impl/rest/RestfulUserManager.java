package com.sportstalk.impl.rest;

import android.annotation.TargetApi;
import android.os.Build;

import com.sportstalk.impl.Utils;
import com.sportstalk.api.chat.IUserManager;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class RestfulUserManager implements IUserManager {

    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulUserManager(SportsTalkConfig sportsTalkConfig) {
        this.setConfig(sportsTalkConfig);
    }

    private void setConfig(SportsTalkConfig sportsTalkConfig) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult listUserMessages(User user, String cursor, int limit) {
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/user/users/" + user.getUserId() + "/?limit=" + limit + "&cursor=" + cursor, apiHeaders, null, sportsTalkConfig.getEventHandler());
        httpClient.setAction("listUserMessages");
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public UserResult setBanStatus(User user, boolean isBanned) {
        Map<String, String> data = new HashMap<>();
        data.put("banned", Boolean.toString(isBanned));
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/user/users/" + user.getUserId() + "/ban", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("banStatus");
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        return createUserResult(jsonObject);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public UserResult createOrUpdateUser(User user) {
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("handle", user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("profileurl", user.getPictureUrl());
        data.put("pictureurl", user.getPictureUrl());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/user/users/" + user.getUserId(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("user");
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        return createUserResult(jsonObject);
    }

    /**
     * @param jsonObject
     * @return
     */
    private UserResult createUserResult(JSONObject jsonObject) {
        UserResult userResult = new UserResult();
        try {
            JSONObject object = jsonObject.getJSONObject("data");
            JSONObject userObject = object.getJSONObject("room");

            userResult.setUserId(userObject.getString("userid"));
            userResult.setKind(Kind.user);
            userResult.setHandle(userObject.getString("handle"));
            userResult.setHandleLowerCase(userObject.getString("handlelowercase"));
            userResult.setDisplayName(userObject.getString("displayname"));
            userResult.setPictureUrl(userObject.getString("pictureurl"));
            userResult.setProfileUrl(userObject.getString("profileurl"));
            userResult.setBanned(userObject.getBoolean("banned"));

        } catch (JSONException ex) {
        }
        return userResult;
    }

    /**
     * lists all users
     */
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUsers() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/user/?limit=100&cursor=", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("listUsers");
        httpClient.execute();
    }

}
