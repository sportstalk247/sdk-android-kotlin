package com.sportstalk.impl.rest;

import android.os.Build;

import com.sportstalk.impl.Utils;
import com.sportstalk.api.conversation.IConversationModerationService;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.ConversationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class RestfulConversationModerationService implements IConversationModerationService {

    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulConversationModerationService(SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getModerationQueueEvents() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", this.sportsTalkConfig.getEndpoint() + "/comment/moderation/queues/comments", apiHeaders, null, sportsTalkConfig.getEventHandler());
        httpClient.setAction("moderationEvent");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void removeEvent(String eventId) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint() + "/moderation/applydecisiontoevent/" + eventId, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("removeEvent");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void approveEvent(String eventId) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint() + "/moderation/applydecisiontoevent/" + eventId, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("removeEvent");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reportEvent(String eventId) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint() + "/moderation/applydecisiontoevent/" + eventId, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("removeEvent");
        httpClient.execute();
    }


    public void setConfig(final SportsTalkConfig config) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Comment> getModerationQueue() {
        ConversationResponse conversationDeletionResponse = new ConversationResponse();
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", this.sportsTalkConfig.getEndpoint() + "/comment/moderation/queues/comments", apiHeaders, data, sportsTalkConfig.getEventHandler());
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray commentsArray = dataObject.getJSONArray("comments");
            int size = commentsArray == null ? 0 : commentsArray.length();
            List<Comment> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = commentsArray.getJSONObject(i);
                list.add(createComment(jsonObject1));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult rejectComment(Comment comment) {
        Map<String, String> data = new HashMap<>();
        data.put("approve", "false");
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", this.sportsTalkConfig.getEndpoint() + "/comment/moderation/queues/comments/" + comment.getId() + "/applydecision", apiHeaders, data, sportsTalkConfig.getEventHandler());
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult approveComment(Comment comment) {
        Map<String, String> data = new HashMap<>();
        data.put("approve", "true");
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", this.sportsTalkConfig.getEndpoint() + "/comment/moderation/queues/comments/" + comment.getId() + "/applydecision", apiHeaders, data, sportsTalkConfig.getEventHandler());
        return httpClient.execute();
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
