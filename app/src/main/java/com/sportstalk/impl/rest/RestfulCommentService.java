package com.sportstalk.impl.rest;

import android.os.Build;

import com.sportstalk.impl.Messages;
import com.sportstalk.impl.Utils;
import com.sportstalk.api.conversation.ICommentService;
import com.sportstalk.error.RequireUserException;
import com.sportstalk.error.SettingsException;
import com.sportstalk.error.ValidationException;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentDeletionResponse;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Commentary;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ReactionResponse;
import com.sportstalk.models.conversation.Vote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

/**
 * This is the service for managing comments via REST.
 * Most of the time you probably want the CommentingClient.
 */
public class RestfulCommentService implements ICommentService {
    private SportsTalkConfig sportsTalkConfig;
    private User user;
    private Map<String, String> apiHeaders;
    private Conversation conversation;
    private String conversationId;

    public RestfulCommentService(SportsTalkConfig sportsTalkConfig) {
        if (sportsTalkConfig != null) setConfig(sportsTalkConfig);
    }

    /**
     * Set the configuration if not set on construction, or to update the configuration.
     * @param config
     */
    @Override
    public void setConfig(SportsTalkConfig config) {
        this.sportsTalkConfig = config;
        this.user = config.getUser();
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }

    /**
     * Set the user to be used in comments.
     * @param user
     */
    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Set the conversation that this comment service will make and read comments to.
     * @param conversation
     * @return
     */
    @Override
    public Conversation setConversation(Conversation conversation) {
        this.conversation = conversation;
        return conversation;
    }

    /**
     * Create a new comment.
     * @param comment the comment to create in the converations
     * @param replyTo optional, if set the comment will be created as a reply to this one.
     * @return the created comment
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Comment create(Comment comment, Comment replyTo) {
        try {
            requireUser();
            requireConversation();
            if (replyTo != null) {
                return makeComment(comment);
            }
            return makeReply(comment, replyTo);
        } catch (RequireUserException e) {
            e.printStackTrace();
        } catch (SettingsException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a comment
     * @param comment the comment to create
     * @return the created comment
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Comment makeComment(Comment comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/").append(this.conversation.getConversationId()).append("/comments");
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("body", comment.getBody());
        StringBuilder tagBuilder = new StringBuilder();
        for (String tag : comment.getTags()) tagBuilder.append(tag).append(",");
        data.put("handle", user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("title", conversation.getTitle());
        data.put("pictureurl", user.getPictureUrl());
        data.put("profileurl", user.getProfileUrl());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        return createComment(jsonObject, "data");
    }

    /**
     * Creates the reply comment
     * @param comment
     * @param replyTo
     * @return the created comment
     * @throws ValidationException
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Comment makeReply(Comment comment, Comment replyTo) throws ValidationException {
        if (replyTo.getId() == null) throw new ValidationException(Messages.MISSING_REPLYTO_ID);

        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(conversation.getConversationId());
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("body", comment.getBody());
        StringBuilder tagBuilder = new StringBuilder();
        for (String tag : comment.getTags()) tagBuilder.append(tag).append(",");
        data.put("tags", tagBuilder.substring(0, tagBuilder.length() - 1));
        data.put("handle", user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("picturerurl", user.getPictureUrl());
        data.put("profileurl", user.getProfileUrl());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();

        return createComment(jsonObject, "data");
    }

    /**
     * Get a specific comment
     * @param comment
     * @return the comment if it exists.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Comment get(Comment comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId());
        Map<String, String> data = new HashMap<>();

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();

        return createComment(jsonObject, "data");
    }

    /**
     * Delete a comment
     * @param comment
     * @return server response.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CommentDeletionResponse delete(Comment comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId());
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("body", comment.getBody());
        //data.put("logicaldelete", "true");

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        CommentDeletionResponse response = new CommentDeletionResponse();
        return response;
    }

    /**
     * Update an existing comment
     * @param comment
     * @return the updated comment
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Comment update(Comment comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId());
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("body", comment.getBody());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "PUT", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        return createComment(jsonObject, "data");
    }

    /**
     * vote on a comment
     * @param comment
     * @param vote
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void vote(Comment comment, Vote vote) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId()).append("/vote");
        Map<String, String> data = new HashMap<>();
        data.put("vote", vote.name());
        data.put("userid", user.getUserId());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
    }

    /**
     * Report a comment for violating community principles.
     * @param comment
     * @param reportType
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void report(Comment comment, ReportType reportType) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId()).append("/report");
        Map<String, String> data = new HashMap<>();
        data.put("reporttype", ReportType.Abuse.name());
        data.put("userid", user.getHandle());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
    }

    /**
     * React to a comment
     * @param comment
     * @param reaction
     * @param enabled
     * @return the reaction response.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ReactionResponse react(Comment comment, Reaction reaction, boolean enabled) {

        ReactionResponse reactionResponse = null;
        try {
            this.requireConversation();
            this.requireUser();


            StringBuilder sb = new StringBuilder();
            sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId()).append("/react");
            Map<String, String> data = new HashMap<>();
            data.put("userid", user.getUserId());
            data.put("reaction", Reaction.like.name());
            data.put("reacted", String.valueOf(true));

            HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
            ApiResult apiResult = httpClient.execute();
            JSONObject jsonObject = (JSONObject) apiResult.getData();

            reactionResponse = new ReactionResponse();

        } catch (SettingsException se) {
        } catch (RequireUserException rue) {
        }

        return reactionResponse;
    }

    /**
     * Gets replies to a comment
     * @param comment
     * @param commentRequest
     * @return the list of comments that were replies.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Comment> getReplies(Comment comment, CommentRequest commentRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments/").append(comment.getId()).append("/replies?cursor&direction=forward&includechildren=false&includeinactive=false");
        Map<String, String> data = new HashMap<>();

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = dataObject.getJSONArray("comments");
            int size = jsonArray == null ? 0 : jsonArray.length();
            List<Comment> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                list.add(createComment(jsonObject1));
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get comments.
     * @param commentRequest
     * @param conversation
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Commentary getComments(CommentRequest commentRequest, Conversation conversation) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/comment/conversations/" + this.conversation.getConversationId() + "/comments?sort=newest&includechildren=false");
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        ApiResult apiResult = httpClient.execute();
        JSONObject jsonObject = (JSONObject) apiResult.getData();
        List<Comment> list = new ArrayList<>();
        Commentary commentary = new Commentary();

        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = dataObject.getJSONArray("comments");
            int size = jsonArray == null ? 0 : jsonArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                list.add(createComment(jsonObject1));
            }

            commentary.setComments(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentary;
    }

    /**
     * Get the conversation
     * @return
     */
    @Override
    public Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Used to enforce having a user set for certain operations
     * @throws RequireUserException
     */
    private void requireUser() throws RequireUserException {
        if (user == null) throw new RequireUserException(Messages.MUST_SET_USER);
        if (user.getUserId() == null || user.getUserId().isEmpty())
            throw new RequireUserException(Messages.USER_NEEDS_ID);
        if (user.getHandle() == null) throw new RequireUserException(Messages.USER_NEEDS_HANDLE);
    }

    /**
     * Used to enforce requiring a conversation to be set for certain operations
     * @throws SettingsException
     */
    private void requireConversation() throws SettingsException {
        if (conversation.getConversationId() == null)
            throw new SettingsException(Messages.NO_CONVERSATION_SET);
    }

    private Comment createComment(JSONObject response) {
        Comment responseComment = new Comment();
        try {
            responseComment.setId(response.getString("id"));
            responseComment.setBody(response.getString("body"));
            if (response.has("replyto"))
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

            responseComment.setVoteScore(response.getInt("votescore"));
            responseComment.setLikeCount(response.getInt("votecount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseComment;
    }

    /**
     * Map JSON to comment object.
     * @param jsonObject
     * @param data
     * @return
     */
    private Comment createComment(JSONObject jsonObject, String data) {
        if (jsonObject == null) return null;
        try {
            return createComment(jsonObject.getJSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
