package com.sportstalk.impl.conversation.rest;

import android.os.Build;

import com.sportstalk.impl.common.rest.Utils;
import com.sportstalk.api.conversation.IConversationModerationService;
import com.sportstalk.impl.rest.HttpClient;
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

/**
 * This is the class used for moderating comments.
 * Most of the time, moderation will be done from the Sportstalk dashboard.
 * However, this is the class you should use to handle moderation programmatically.
 */
public class RestfulCommentsModerationService implements IConversationModerationService {

    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    /**
     * Create a new moderation service for your app.
     * @param sportsTalkConfig
     */
    public RestfulCommentsModerationService(SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    public void setConfig(final SportsTalkConfig config) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = Utils.getApiHeaders(sportsTalkConfig.getApiKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Comment> getModerationQueue() {
        ConversationResponse conversationDeletionResponse = new ConversationResponse();
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", this.sportsTalkConfig.getEndpoint()  + "/" + this.sportsTalkConfig.getAppId() + "/comment/moderation/queues/comments", apiHeaders, data, sportsTalkConfig.getEventHandler());
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray commentsArray = dataObject.getJSONArray("comments");
            int size = commentsArray == null ? 0 : commentsArray.length();
            List<Comment> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = commentsArray.getJSONObject(i);
                list.add(mapCommentResponse(jsonObject1));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reject a comment in the queue.  It will not be sent to users.
     * @param comment
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult rejectComment(Comment comment) {
        Map<String, String> data = new HashMap<>();
        data.put("approve", "false");
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint()  + "/" + this.sportsTalkConfig.getAppId() + "/comment/moderation/queues/comments/" + comment.getId() + "/applydecision", apiHeaders, data, sportsTalkConfig.getEventHandler());
        return httpClient.execute();
    }

    /**
     * Approve a comment in the queue.  It will now show in the conversation.
     * @param comment
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult approveComment(Comment comment) {
        Map<String, String> data = new HashMap<>();
        data.put("approve", "true");
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint()  + "/" + this.sportsTalkConfig.getAppId() + "/comment/moderation/queues/comments/" + comment.getId() + "/applydecision", apiHeaders, data, sportsTalkConfig.getEventHandler());
        return httpClient.execute();
    }

    /**
     * Map a comment response.
     * TODO: Automate all of these with GSON or similar library.
     * @param response
     * @return the mapped comment.
     */
    private Comment mapCommentResponse(JSONObject response) {
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
