package com.mcsolo.tests;

import com.mcsolo.helpers.TestDataLoader;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * CRUD coverage for the /posts resource on JSONPlaceholder.
 * Note: JSONPlaceholder is a fake API, it accepts writes and echoes them
 * back, but nothing is really saved on the server.
 * Payloads and expected values live in test-data/posts.json and
 * test-data/users.json and are loaded via TestDataLoader.
 */
public class PostsCrudTest extends BaseTest {

    @Test
    public void getAllPosts_returnsList() {
        postsClient.getAllPosts()
        .then()
                .spec(okJsonResponse())
                .body("size()", equalTo(TestDataLoader.expectedTotalPosts()))
                .body("[0].id", equalTo(TestDataLoader.knownPostId()))
                .body("userId", everyItem(notNullValue()));
    }

    @Test
    public void getPostsFilteredByUser_returnsOnlyThatUsersPosts() {
        int userId = TestDataLoader.filterUserId();
        postsClient.getPostsByUser(userId)
        .then()
                .spec(okJsonResponse())
                .body("size()", equalTo(TestDataLoader.expectedPostsForUser(userId)))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test
    public void getPostById_returnsSinglePost() {
        int postId = TestDataLoader.knownPostId();
        postsClient.getPostById(postId)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(postId))
                .body("userId", equalTo(TestDataLoader.knownPostUserId()))
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    @Test
    public void createPost_returns201WithEchoedBody() {
        Map<String, Object> payload = TestDataLoader.postPayload("createPost");
        postsClient.createPost(payload)
        .then()
                .statusCode(201)
                .contentType("application/json; charset=utf-8")
                .body("title", equalTo(payload.get("title")))
                .body("body", equalTo(payload.get("body")))
                .body("userId", equalTo(payload.get("userId")))
                .body("id", notNullValue());
    }

    @Test
    public void updatePostWithPut_returns200WithReplacedBody() {
        Map<String, Object> payload = TestDataLoader.postPayload("updatePost");
        postsClient.updatePost(TestDataLoader.knownPostId(), payload)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(TestDataLoader.knownPostId()))
                .body("title", equalTo(payload.get("title")))
                .body("body", equalTo(payload.get("body")));
    }

    @Test
    public void updatePostWithPatch_returns200WithPatchedField() {
        Map<String, Object> payload = TestDataLoader.postPayload("patchPost");
        postsClient.patchPost(TestDataLoader.knownPostId(), payload)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(TestDataLoader.knownPostId()))
                .body("title", equalTo(payload.get("title")));
    }

    @Test
    public void deletePost_returns200() {
        postsClient.deletePost(TestDataLoader.knownPostId())
        .then()
                .statusCode(200)
                .body(equalTo("{}"));
    }
}
