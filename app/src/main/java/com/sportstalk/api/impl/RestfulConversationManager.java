package com.sportstalk.api.impl;

import android.os.Build;

import com.android.volley.VolleyError;
import com.sportstalk.Utils;
import com.sportstalk.api.conversation.IConversationManager;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.ModerationType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.ConversationResponse;
import com.sportstalk.rest.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class RestfulConversationManager implements IConversationManager {


    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulConversationManager(SportsTalkConfig config) {
        this.sportsTalkConfig = config;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ConversationResponse createConversation(Conversation conversation) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations");
        Map<String, String> data = new HashMap<>();
        data.put("conversationid", conversation.getConversationId());
        data.put("owneruserid", conversation.getOwnerUserId());
        data.put("property", conversation.getProperty());
        data.put("moderation", conversation.getModerationType().name());
        data.put("maxreports", conversation.getMaxReports() + "");
        data.put("title", conversation.getTitle());
        data.put("maxcommentlen", conversation.getMaxCommentLen() + "");
        data.put("conversationisopen", String.valueOf(conversation.isConversationIsOpen()));
        StringBuilder tagBuilder = new StringBuilder();
        for (String tag : conversation.getTags()) tagBuilder.append(tag).append(",");
        data.put("customid", conversation.getCustomId());
        data.put("udf1", conversation.getUdf1());
        data.put("udf2", conversation.getUdf2());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        ConversationResponse response = null;
        if (apiResult.getErrors() != null) return response;
        return createConversationResponse(jsonObject, "data");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ConversationResponse getConversation(Conversation conversation) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + conversation.getConversationId());
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        if (apiResult.getErrors() != null) return null;
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        return createConversationResponse(jsonObject, "data");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Conversation> getConversationsByProperty(String property) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment?propertyid=").append(property);
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        List<Conversation> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("conversations");
            int size = jsonArray == null ? 0 : jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject conversionObject = jsonArray.getJSONObject(i);
                list.add(createConversationResponse(conversionObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ConversationListResponse listConversations() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations");
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        List<Conversation> list = new ArrayList<>();
        ConversationListResponse conversationListResponse = new ConversationListResponse();

        try {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("conversations");
            int size = jsonArray == null ? 0 : jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject conversionObject = jsonArray.getJSONObject(i);
                list.add(createConversationResponse(conversionObject));
            }
            conversationListResponse.setConversations(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conversationListResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ConversationListResponse listConversationsByCustomer(String customId) {

        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/find/conversation/bycustomid?customid=" + customId);
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        List<Conversation> list = new ArrayList<>();
        ConversationListResponse conversationListResponse = new ConversationListResponse();

        try {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("conversations");
            int size = jsonArray == null ? 0 : jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject conversionObject = jsonArray.getJSONObject(i);
                list.add(createConversationResponse(conversionObject));
            }
            conversationListResponse.setConversations(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conversationListResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ConversationDeletionResponse deleteConversation(Conversation conversation) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/").append(conversation.getConversationId());
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = null;
        ConversationDeletionResponse response = null;
        try {
            if (apiResult.getErrors() != null) {
                VolleyError volleyError = (VolleyError) apiResult.getErrors();
                jsonObject = new JSONObject(new String(volleyError.networkResponse.data));
            } else {
                jsonObject = (JSONObject) apiResult.getData();
            }

            response = new ConversationDeletionResponse();

            JSONObject responseObject = jsonObject.getJSONObject("data");
            response.setConversationId(responseObject.getString("conversationid"));
            response.setUserid(responseObject.getString("userid"));
            if (responseObject.has("deletedcomments"))
                response.setDeletedComments(responseObject.getInt("deletedcomments"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void setConfig(SportsTalkConfig config) {
        this.sportsTalkConfig = config;
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }

    private ConversationResponse createConversationResponse(JSONObject jsonObject, String data) {
        ConversationResponse response = new ConversationResponse();
        try {
            JSONObject responseObject = jsonObject.getJSONObject(data);
            response = createConversationResponse(responseObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    private ConversationResponse createConversationResponse(JSONObject jsonObject) {
        ConversationResponse response = new ConversationResponse();
        try {
            response.setKind(Kind.conversation);
            response.setAppId(jsonObject.getString("appid"));
            response.setOwnerUserId(jsonObject.getString("owneruserid"));
            response.setConversationId(jsonObject.getString("conversationid"));
            response.setProperty(jsonObject.getString("property"));
            response.setModerationType(ModerationType.valueOf(jsonObject.getString("moderation")));
            response.setMaxReports(jsonObject.getInt("maxreports"));
            response.setTitle(jsonObject.getString("title"));
            response.setMaxCommentLen(jsonObject.getInt("maxcommentlen"));
            response.setConversationIsOpen(jsonObject.getBoolean("open"));

            List<String> tags = new ArrayList<>();
            String sTag = jsonObject.getString("tags");
            tags.add(sTag);
            response.setTags(tags);

            response.setCustomId(jsonObject.getString("customid"));
            response.setUdf1(jsonObject.getString("udf1"));
            response.setUdf2(jsonObject.getString("udf2"));
            response.setCommentCount(jsonObject.getInt("commentcount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    private Comment createComment(JSONObject response) {
        Comment responseComment = new Comment();
        try {
            responseComment.setId(response.getString("id"));
            responseComment.setBody(response.getString("body"));
            responseComment.setReplyTo(response.getString("replyto"));
            // gets user details
            JSONObject userObject = response.optJSONObject("user");
            responseComment.setKind(Kind.user);
            responseComment.setUserId(userObject.getString("userid"));
            responseComment.setHandle(userObject.getString("handle"));
            responseComment.setHandleLowerCase(userObject.getString("handlelowercase"));
            responseComment.setDisplayName(userObject.getString("displayname"));
            responseComment.setPictureUrl(userObject.getString("pictureurl"));
            responseComment.setProfileUrl(userObject.getString("profileurl"));
            responseComment.setBanned(userObject.getBoolean("banned"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseComment;
    }

}
