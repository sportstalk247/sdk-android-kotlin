package com.sportstalk;

import android.content.Context;

import com.sportstalk.error.SportsTalkException;
import com.sportstalk.models.common.ModerationType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.Vote;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
public class AndroidTestConversation {

    Context context;
    SportsTalkConfig sportsTalkConfig;
    User user;
    ChatClient chatClient;

    CommentingClient conversationClient;

    String conversationTestId = "api-conversation-test-demo2";

    @Before
    public void setup() {

        context = InstrumentationRegistry.getInstrumentation().getContext();

        sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("ZortLH1JUkuEJQ5YgkZjHwx-AZFkPSJkSYnEO1NA6y7A");
        sportsTalkConfig.setContext(context);
        sportsTalkConfig.setAppId("5e9eff5338a28719345eb469");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");

        user = new User();
        user.setUserId("sarah");
        user.setDisplayName("sarah");
        user.setHandle("sarah");
        user.setHandleLowerCase("sarah");
        sportsTalkConfig.setUser(user);
        chatClient = ChatClient.create(sportsTalkConfig);

        conversationClient = CommentingClient.create(sportsTalkConfig, null, null, null);

    }

    @Test
    public void createConversationTest() {
        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("/api");
        // conv.setOwnerUserId("sarah");
        Conversation createdConversation = conversationClient.createConversation(conv, true);
        Assert.assertTrue(createdConversation.getConversationId().equals(conversationTestId));
    }

    @Test
    public void listConversationTest() {
        ConversationListResponse conversationListResponse = conversationClient.listConversations();
        int size = conversationListResponse.getConversations().size();
        Assert.assertTrue(size > 0);
    }


    @Test
    public void testForInvalidComment() {

        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        comment.setId("test-comment-id");
        Comment result = conversationClient.getComment(comment);
        Assert.assertNull(result);
    }

    //@Test
    public void whenThereIsValidCommentThenCommentIdShouldNoBlank() {

        Conversation conv = new Conversation();
        conv.setConversationId("api-conversation-test-demo2");
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setMaxCommentLen(100);
        conv.setOwnerUserId("sarah");
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        //comment.setId("test-comment-id");
        comment.setBody("test");

        List<String> tags = new ArrayList<>();
        tags.add("taga");
        comment.setTags(tags);
        Comment result = conversationClient.createComment(comment);
        Assert.assertNotNull(result.getId());
    }

    //@Test
    public void whenReplyToIsNotEmptyThenGeneratedCommentShouldNotBeNull() {

        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        comment.setId("test-comment-id");
        comment.setBody("test");
        List<String> tags = new ArrayList<>();
        tags.add("taga");
        comment.setTags(tags);
        comment.setReplyTo("sarah");
        Comment result = conversationClient.createComment(comment);
        Assert.assertNotNull(result);
    }

    @Test
    public void whenConversationIdIsNullThenCreatedConversationIsNull() {
        Conversation conv = new Conversation();
        //conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        Conversation createdConversation = conversationClient.createConversation(conv, true);
        Assert.assertNull(createdConversation);
    }

    @Test
    public void whenCommentBodyIsEmptyThenGeneratedConversationIsNull() {
        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        comment.setBody("");
        List<String> tags = new ArrayList<>();
        tags.add("");
        comment.setTags(tags);
        Comment result = conversationClient.createComment(comment);
        Assert.assertNull(result);
    }

    @Test
    public void whenConversationIdIsNullThenTheCommentsShouldBeEmpty() {

        Conversation conv = new Conversation();
        //conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);
        CommentRequest commentRequest = new CommentRequest();
        List<Comment> list = conversationClient.getComments(commentRequest, conv);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void whenConversationIdIsNullThenSelectedConversationIsNull() {
        Conversation conv = new Conversation();
        //conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);
        CommentRequest commentRequest = new CommentRequest();

        Conversation conversation = conversationClient.getConversation(conv);
        Assert.assertNull(conversation);
    }

    @Test
    public void whenConversationIdIsNullThenDeletedCommentsShouldBeZero() {

        Conversation conv = new Conversation();
        //conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        ConversationDeletionResponse response = conversationClient.deleteConversation(conv);

        Assert.assertEquals(0, response.getDeletedComments());
    }

    @Test
    public void whenConversationIdIsSetThenGetConversationShouldNotBeNull() {

        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Conversation response = conversationClient.getConversation(conv);
        Assert.assertNotNull(response);

    }

    @Test
    public void whenCommentIdIsNullThenCommetRepliesShouldBeEmpty() {
        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        CommentRequest commentRequest = new CommentRequest();
        List<Comment> list = conversationClient.getCommentReplies(comment, commentRequest);
        Assert.assertTrue(list.isEmpty());
    }

    // @Test
    public void whenConversationIdIsNullThenCommentRepliesShuldBeEmpty() {

        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        comment.setBody("test comment only");
        List<String> tags = new ArrayList<>();
        tags.add("taga");
        comment.setTags(tags);
        Comment response = conversationClient.createComment(comment);
        conv.setConversationId(null);
        conversationClient.setConversation(conv);
        CommentRequest commentRequest = new CommentRequest();
        List<Comment> list = conversationClient.getCommentReplies(response, commentRequest);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void whenCommentIdIsNullThenVoteOnCommentShouldBeNull() {
        Conversation conv = new Conversation();
        conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        comment.setBody("test comment only");
        List<String> tags = new ArrayList<>();
        tags.add("taga");
        comment.setTags(tags);
        comment.setId(null);

        Comment response = conversationClient.voteOnComment(comment, Vote.up);
        Assert.assertNull(response.getId());
    }

    @Test
    public void whenCoversationIdIsNullThenUpdatedCommentIsNull() {
        Conversation conv = new Conversation();
        //conv.setConversationId(conversationTestId);
        conv.setModerationType(ModerationType.post);
        conv.setMaxReports(3);
        conv.setConversationIsOpen(true);
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conv.setTags(tgs);
        conv.setProperty("sportstalk247.com/apidemo");
        conversationClient.setConversation(conv);

        Comment comment = new Comment();
        Comment response = conversationClient.updateComment(comment);
        Assert.assertNull(response);
    }

    @Test
    public void whenCommentLengthIsZeroOfConversationThenGeneratedCommentIsNull() {
        Conversation conversation = new Conversation();
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conversation.setTags(tgs);
        conversation.setConversationId("api-conversation-test-demo2");
        Conversation returnConversation = conversationClient.getConversation(conversation);
        returnConversation.setTags(tgs);
        returnConversation.setMaxCommentLen(0);
        returnConversation.setOwnerUserId("sarah");
        Conversation updated = conversationClient.createConversation(returnConversation, false);
        Assert.assertEquals(0, updated.getMaxCommentLen());

        // create comment
        Comment comment = new Comment();
        comment.setBody("this is a comment for testing");
        comment.setTags(tgs);
        conversationClient.setConversation(updated);
        Comment nComment = conversationClient.createComment(comment);
        Assert.assertNull(nComment);
    }

    //@Test
    public void whenUserIdIsNullThenConversationIsNull() {
        Conversation conversation = new Conversation();
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conversation.setTags(tgs);
        conversation.setMaxCommentLen(100);
        conversation.setConversationId("api-conversation-test-demo2");
        conversation.setModerationType(ModerationType.post);
        Conversation conv = conversationClient.createConversation(conversation, true);
        Assert.assertNull(conv);
    }

    @Test(expected = SportsTalkException.class)
    public void WhenNoCommentForConversationThenItWillThrowException() {
        Conversation conversation = new Conversation();
        List<String> tgs = new ArrayList<>();
        tgs.add("taga");
        tgs.add("tagb");
        conversation.setTags(tgs);
        conversation.setMaxCommentLen(100);
        conversation.setConversationId("api-conversation-test-demo2");
        conversation.setModerationType(ModerationType.post);
        conversation.setOwnerUserId("sarah");
        conversationClient.setConversation(conversation);

        // gets a particular comment
        Comment comment = new Comment();
        comment.setId("test");
        comment.setTags(tgs);

        Comment returnComment = conversationClient.getComment(comment);
    }

}
