package com.sportstalk.impl.rest;

import android.annotation.TargetApi;
import android.os.Build;

import com.sportstalk.impl.Utils;
import com.sportstalk.api.chat.IUserManager;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.SearchType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Override
    public List<User> listUsers(int limit, String cursor) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/user/users/?limit="+limit+"&cursor=" + cursor, apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("listUsers");
        JSONObject jsonObject = (JSONObject)httpClient.execute().getData();
        List<User> list = new ArrayList<>();
        try {

            JSONArray array = jsonObject.getJSONObject("data").getJSONArray("users");
            int size = array == null ? 0 : array.length();

            for(int i = 0; i<size; i++) {
                JSONObject userObject = array.getJSONObject(i);
                User user = new User();
                user.setKind(Kind.user);
                user.setUserId(userObject.getString("userid"));
                user.setHandle(userObject.getString("handle"));
                user.setHandleLowerCase(userObject.getString("handlelowercase"));
                user.setDisplayName(userObject.getString("displayname"));
                user.setPictureUrl(userObject.getString("pictureurl"));
                user.setProfileUrl(userObject.getString("profileurl"));
                user.setBanned(userObject.getBoolean("banned"));

                list.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * deletes a user based on id
     * @param user
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public UserResult deleteUser(User user) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sportsTalkConfig.getEndpoint() + "/user/users/" + user.getUserId(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        UserResult delUser = null;
        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject userObject = dataObject.getJSONObject("user");
            delUser = new UserResult();
            delUser.setKind(Kind.user);
            delUser.setUserId(userObject.getString("userid"));
            delUser.setHandle(userObject.getString("handle"));
            delUser.setHandleLowerCase(userObject.getString("handlelowercase"));
            delUser.setDisplayName(userObject.getString("displayname"));
            delUser.setPictureUrl(userObject.getString("pictureurl"));
            delUser.setProfileUrl(userObject.getString("profileurl"));
            delUser.setProfileUrl(userObject.getString("banned"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return delUser;
    }

    /**
     * search users based on id, name and handle
     * @param searchType
     * @param limit
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<User> searchUsers(SearchType searchType, int limit) {
        Map<String, String> data = new HashMap<>();
        if(searchType.getHandle() != null)
            data.put("handle", searchType.getHandle());
        else if(searchType.getName() != null)
            data.put("name", searchType.getName());
        else if(searchType.getUserId() != null)
            data.put("userid", searchType.getUserId());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/user/search", apiHeaders, data, sportsTalkConfig.getApiCallback());
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();

        List<User> list = new ArrayList<>();
        try {
            JSONArray array = jsonObject.getJSONObject("data").getJSONArray("users");
            int size = array == null ? 0 : array.length();

            for(int i = 0; i<size; i++) {
                JSONObject userObject = array.getJSONObject(i);
                User user = new User();
                user.setKind(Kind.user);
                user.setUserId(userObject.getString("userid"));
                user.setHandle(userObject.getString("handle"));
                user.setHandleLowerCase(userObject.getString("handlelowercase"));
                user.setDisplayName(userObject.getString("displayname"));
                user.setPictureUrl(userObject.getString("pictureurl"));
                user.setProfileUrl(userObject.getString("profileurl"));
                user.setBanned(userObject.getBoolean("banned"));

                list.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
