package com.sportstalk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
//import android.support.v7.app.AppCompatActivity;

import com.sportstalk.api.RoomUserResult;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.ModerationType;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Commentary;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.Vote;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class MainActivity1 extends Activity {

    private static final String TAG = MainActivity1.class.getName();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SportsTalkConfig sportsTalkConfig       = new SportsTalkConfig();
            sportsTalkConfig.setApiKey("QZF6YKDKSUCeL03tdA2l2gx4ckSvC7LkGsgmix-pBZLA");
        sportsTalkConfig.setContext(MainActivity1.this.getApplicationContext());
        sportsTalkConfig.setAppId("5e92a5ce38a28d0b6453687a");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");

        final EventHandler eventHandler = new EventHandler() {
            @Override
            public void onEventStart(Event event) {
                System.out.println(".. onevent started..");
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
                System.out.println("... onSpeech...");
            }

            @Override
            public void onChat(Event event) {
             System.out.println("... onChat...");
            }

            @Override
            public void onNetworkResponse(List<EventResult> list) {
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

        final User user = new User();
        user.setUserId("001864a867604101b29672e904da688a");
        user.setDisplayName("Aldo");
        user.setHandle("Aldo");
        user.setHandleLowerCase("aldo");
        sportsTalkConfig.setUser(user);

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                ChatClient chatClient = ChatClient.create(sportsTalkConfig);
                System.out.println(" ... chat client " + chatClient);
                Conversation  testConversation  = new Conversation();
                testConversation.setConversationId("api-conversation-demo1");
                ConversationClient conversationClient = ConversationClient.create(sportsTalkConfig,testConversation,null,null);

                Log.d(TAG, "*** Creating conversation ....***");
                Conversation conv = new Conversation();
                conv.setConversationId("api-conversation-demo2");
                conv.setModerationType(ModerationType.post);
                conv.setMaxReports(3);
                conv.setConversationIsOpen(true);
                List<String>tgs = new ArrayList<>();
                tgs.add("taga");
                tgs.add("tagb");
                conv.setTags(tgs);
                conv.setProperty("sportstalk247.com/apidemo");
                Conversation createdConversation = conversationClient.createConversation(conv, true);
                Log.d(TAG, "*** NEW conversation id ....***" + createdConversation.getConversationId());
                conversationClient.setConversation(createdConversation);

                Log.d(TAG, "*** conversation created ....***");

                //SportsTalkClient sportsTalkClient = SportsTalkClient.create(sportsTalkConfig);
                //List<Room>rooms = sportsTalkClient.listRooms(null);
                //System.out.println(" ...rooms size..." + rooms.size());

                // 1. join room
                   Log.d(TAG, "*** 1. join room");
                   RoomResult roomResult = new RoomResult();
                   roomResult.setId("5dd9d5a038a28326ccfe5743");
                   RoomUserResult roomUserResult = chatClient.joinRoom(roomResult);
                   System.out.println(" .... what is new room user result.." + roomUserResult.getRoomResult().getId());
                   chatClient.setRoom(roomUserResult.getRoomResult());

                // 2. start talk
                    //chatClient.startTalk();

                // 3. report comment: Need event id and get it from
                // any event

                      Log.d(TAG, "*** 3. report comment");
                      // set the current room
                     chatClient.setRoom(roomUserResult.getRoomResult());
                      EventResult eventResult = new EventResult();
                      eventResult.setId("5e53f65638a2830e84208f2a");
                      eventResult.setUser(user);

                      ApiResult apiResult = chatClient.report(eventResult, ReportType.Abuse);
                     // 4. make reply

                // 5. list comments
                      Log.d(TAG, "*** 5. list comments");
                   CommentRequest commentRequest = new CommentRequest();
                   Conversation conversation = new Conversation();
                   List<Comment>list = conversationClient.getComments(commentRequest, conversation);
                   Log.d(TAG, "... comment list.. " + list.size());
                   for(Comment comment: list) {
                       Log.d(TAG,"comment id " + comment.getId());
                   }

                // 6. create comment
                      Log.d(TAG, "*** 6. create comment");
                      Comment comment = new Comment();
                      comment.setBody("Intl....");
                      List<String>tags = new ArrayList<>();
                      tags.add("taga");
                      tags.add("tagb");
                      comment.setTags(tags);
                      Comment nComment = conversationClient.makeComment(user.getUserId(), comment );
                      if(nComment != null)
                      Log.d(TAG, "  nComment... " + nComment.getId());

                // 7. update comment
                      // change the comment body
                      Log.d(TAG, "*** 7. Update comment");
                      if(nComment != null) {
                          nComment.setBody(" new comment body....");
                          Comment updatedComment = conversationClient.updateComment(nComment);
                      }

                // 8. send command
                    Log.d(TAG, "*** 8. send command");
                CommandOptions commandOptions = new CommandOptions();
                commandOptions.setReplyTo(roomUserResult.getRoomResult().getId());
                Map<String, String> data = new HashMap<>();
                apiResult = chatClient.sendCommand("hello", commandOptions);

                //sportsTalkClient.sendCommand("hello", commandOptions, "5dd9d5a038a28326ccfe5743");
                // 9. get comment
                Log.d(TAG, "*** 9. get comment");
                      Comment c = conversationClient.getComment(nComment);
                // 10. get comments
                    Log.d(TAG, "*** 10. get comments");
                   List<Comment>comments = conversationClient.getComments(commentRequest, conversation);

                   // 11. react
                    Log.d(TAG, "*** 11. react to comment");
                    if(nComment != null) {
                        Comment reactedComment = conversationClient.reactToComment(nComment, Reaction.like);
                        System.out.println("..reaction to comment.. " + reactedComment);
                    }

                 // 12. vote
                    Log.d(TAG, "*** 12. vote comment");
                // TODO: this will return a server side error saying to try again later
                    if(nComment != null) {
                        Comment votedComment = conversationClient.voteOnComment(nComment, Vote.up);
                        System.out.println("... voted comment.. " + votedComment);
                    }else Log.d(TAG, "comment object is null");

                 // 13. delete comment
                Log.d(TAG, "*** 13. delete comment");
                if(nComment != null) {
                    nComment.setId("5e941c7a38a2b10794e2722f");
                    conversationClient.deleteComment(nComment);
                    Log.d(TAG, "comment deleted");
                }
                 // 14. get comment replies
                 Log.d(TAG, "*** 14 get replies..");
                List<Comment>comments1 = conversationClient.getCommentReplies(comment, commentRequest);
                System.out.println(" comment replies... " + comments1);

                return null;
            }
        }.execute();
    }
}
