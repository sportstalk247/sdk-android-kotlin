package com.sportstalk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.ModerationType;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.Vote;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class TestingActivity extends Activity {

    private static final String TAG = TestingActivity.class.getName();

    private String roomId = null;
    private ChatClient client;

    private int i = 1;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final User user = new User();
        user.setUserId("sarah");
        user.setDisplayName("sarah");
        user.setHandle("sarah");
        user.setHandleLowerCase("sarah");

        final SportsTalkConfig sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("QZF6YKDKSUCeL03tdA2l2gx4ckSvC7LkGsgmix-pBZLA"); //QZF6YKDKSUCeL03tdA2l2gx4ckSvC7LkGsgmix-pBZLA
        sportsTalkConfig.setContext(TestingActivity.this.getApplicationContext());
        sportsTalkConfig.setAppId("5e92a5ce38a28d0b6453687a"); //5e92a5ce38a28d0b6453687a

        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3"); //https://api.sportstalk247.com/api/v3

        final EventHandler eventHandler = new EventHandler() {
            @Override
            public void onEventStart(Event event) {
                System.out.println(".. onevent started.." + event.getBody());

                new AsyncTask() {
                    @Override
                    protected Void doInBackground(Object... objects) {
                        if (i == 1) {
                            CommandOptions commandOptions = new CommandOptions();
                            commandOptions.setReplyTo(roomId);
                            Log.d(TAG, "**** going to send command");
                            ApiResult apiResult = client.sendCommand("hello", commandOptions);
                            Log.d(TAG, " **** message... " + apiResult.getMessage());
                        }


                        i += 1;
                        return null;
                    }
                }.execute();
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
                new AsyncTask() {
                    @Override
                    protected Void doInBackground(Object... objects) {
                        if (i == 1) {
                            CommandOptions commandOptions = new CommandOptions();
                            commandOptions.setReplyTo(roomId);
                            Log.d(TAG, "**** going to send command");
                            ApiResult apiResult = client.sendCommand("hello", commandOptions);
                            Log.d(TAG, " **** message... " + apiResult.getMessage());
                        }

                        i += 1;
                        return null;
                    }
                }.execute();

            }

            @Override
            public void onChat(Event event) {
                System.out.println(".. onchat event started..");
            }

            @Override
            public void onNetworkResponse(List<EventResult> list) {
                System.out.println(".. onnetwork response ..");
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
        sportsTalkConfig.setUser(user);

        new AsyncTask() {
            @Override
            protected Void doInBackground(Object... objects) {
                client = ChatClient.create(sportsTalkConfig);
                ConversationClient conversationClient = ConversationClient.create(sportsTalkConfig, null, null, null);

                Room room = new Room();
                room.setName("Test Room");
                room.setSlug("chat-test-room");

                RoomResult roomResult = client.createRoom(room);
                Log.d(TAG, ".. room created.. " + roomResult.getId());

                RoomUserResult roomUserResult = client.joinRoom(roomResult);
                client.setRoom(roomUserResult.getRoomResult());
                roomId = roomResult.getId();

                Log.d(TAG, ".. joined room.. " + roomResult.getId());

                EventResult eventResult = client.listUserMessages(user, roomUserResult.getRoomResult(), "", 100);
                Log.d(TAG, "event result " + eventResult.getBody());
                client.startTalk(); // This step is mandatory, otherwise cannot send command

                Log.d(TAG, ".. started talk.. ");

                addDelay();
                addDelay();

                Conversation conv = new Conversation();
                conv.setConversationId("api-conversation-demo2");
                conv.setModerationType(ModerationType.post);
                conv.setMaxReports(3);
                conv.setConversationIsOpen(true);
                List<String> tgs = new ArrayList<>();
                tgs.add("taga");
                tgs.add("tagb");
                conv.setTags(tgs);
                conv.setProperty("sportstalk247.com/apidemo");
                Conversation createdConversation = conversationClient.createConversation(conv, true);
                Log.d(TAG, "*** NEW conversation id ....***" + createdConversation.getConversationId());
                conversationClient.setConversation(createdConversation);

                CommandOptions commandOptions = new CommandOptions();
                commandOptions.setReplyTo(roomUserResult.getRoomResult().getId());

                ApiResult apiResult = client.sendCommand("hello", commandOptions);
                Log.d(TAG, " message... " + apiResult.getMessage());

                Log.d(TAG, "stop talk....");
                client.stopTalk();

                Log.d(TAG, "now creating conversation...");
                doConversationTestSequences(sportsTalkConfig);

                return null;
            }
        }.execute();
    }

    private void doConversationTestSequences(SportsTalkConfig sportsTalkConfig) {
        Conversation conversation = new Conversation();
        conversation.setConversationId("api-conversation-demo2");
        conversation.setOwnerUserId("001864a867604101b29672e904da688a");
        conversation.setModerationType(ModerationType.post);
        conversation.setProperty("testing");
        conversation.setMaxReports(3);
        conversation.setTitle("Test conversation");
        conversation.setMaxCommentLen(512);
        conversation.setConversationIsOpen(true);
        List<String> tags = new ArrayList<>();
        tags.add("taga");
        tags.add("tagb");
        conversation.setTags(tags);
        conversation.setUdf1("/sample/userdefined1");
        conversation.setUdf2("/sample/userdefined2");

        ConversationClient conversationClient = ConversationClient.create(sportsTalkConfig, conversation, null, null);
        Comment comment = new Comment();
        comment.setBody("this is a test comment");
        comment.setTags(tags);
        Comment userComment = conversationClient.makeComment("", comment);
        Log.d(TAG, "user created a new comment..");

        Log.d(TAG, "...react to comment...");
        Comment responseComment = conversationClient.reactToComment(userComment, Reaction.like);

        Log.d(TAG, "...reply to the comment...");
        conversationClient.makeComment("", comment);

        Log.d(TAG, "get all comments");
        CommentRequest commentRequest = new CommentRequest();
        List<Comment> list = conversationClient.getComments(commentRequest, null);
        System.out.println(" total comments ... " + list.size());

        Log.d(TAG, "get all comment replies");
        List<Comment> replies = conversationClient.getCommentReplies(comment, null);
        Log.d(TAG, " ... comment replies " + replies.size());

        Log.d(TAG, "get single comment...");
        Comment singleComment = conversationClient.getComment(list.get(1));
        Log.d(TAG, " ... single comment  " + singleComment);

        Log.d(TAG, ".... voting comment....");
        Comment votedComment = conversationClient.voteOnComment(singleComment, Vote.up);
        Log.d(TAG, ".... voted comment...." + votedComment);

        Log.d(TAG, " creating one more conversation id");
        Conversation secondConversation = new Conversation();
        Conversation conv = new Conversation();
        secondConversation.setConversationId("api-conversation-demo3");
        secondConversation.setModerationType(ModerationType.post);
        secondConversation.setMaxReports(3);
        secondConversation.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        secondConversation.setTags(tgs);
        secondConversation.setProperty("sportstalk247.com/apidemo");
        Conversation createdConversation = conversationClient.createConversation(secondConversation, true);
        Log.d(TAG, "*** SECOND conversation id ....***" + createdConversation.getConversationId());

        Log.d(TAG, "*** getting single conversations ...");
        Conversation singleConv = conversationClient.getConversation(createdConversation);
        Log.d(TAG, "... getting single conv " + singleConv.getConversationId());

        Log.d(TAG, " getting all conversations.....");
        ConversationListResponse listResponse = conversationClient.listConversations();
        Log.d(TAG, " *** conversations... " + listResponse.getConversations().size());

        Log.d(TAG, "deleting the conversation");
        ConversationDeletionResponse response = conversationClient.deleteConversation(conversation);
        Log.d(TAG, "deleting the conversation " + response.getConversationId());

    }

    private void addDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
