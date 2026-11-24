package com.mcsolo.tests;

import com.mcsolo.helpers.TestDataLoader;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * CRUD coverage for the /users resource on JSONPlaceholder, plus the
 * /comments endpoints that belong to a post.
 * Note: JSONPlaceholder is a fake API, it accepts writes and echoes them
 * back, but nothing is really saved on the server.
 * Payloads and expected values live in test-data/users-crud.json and
 * are loaded via TestDataLoader.
 */
public class UsersCrudTest extends BaseTest {

    @Test
    public void getAllUsers_returnsList() {
        usersClient.getAllUsers()
        .then()
                .spec(okJsonResponse())
                .body("size()", equalTo(TestDataLoader.expectedTotalUsers()))
                .body("[0].id", equalTo(TestDataLoader.knownUserId()))
                .body("[0].name", equalTo(TestDataLoader.knownUserName()))
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(not(emptyOrNullString())))
                .body("username", everyItem(not(emptyOrNullString())))
                .body("email", everyItem(not(emptyOrNullString())));
    }

    @Test
    public void getUserById_returnsSingleUser() {
        int userId = TestDataLoader.knownUserId();
        usersClient.getUserById(userId)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(userId))
                .body("name", equalTo(TestDataLoader.knownUserName()))
                .body("username", equalTo(TestDataLoader.knownUserUsername()))
                .body("email", equalTo(TestDataLoader.knownUserEmail()))
                .body("address", notNullValue())
                .body("address.city", not(emptyOrNullString()))
                .body("company.name", not(emptyOrNullString()));
    }

    @Test
    public void createUser_returns201WithEchoedBody() {
        Map<String, Object> payload = TestDataLoader.userPayload("createUser");
        usersClient.createUser(payload)
        .then()
                .statusCode(201)
                .contentType("application/json; charset=utf-8")
                .body("name", equalTo(payload.get("name")))
                .body("username", equalTo(payload.get("username")))
                .body("email", equalTo(payload.get("email")))
                .body("id", notNullValue());
    }

    @Test
    public void updateUserWithPut_returns200WithReplacedBody() {
        Map<String, Object> payload = TestDataLoader.userPayload("updateUser");
        usersClient.updateUser(TestDataLoader.knownUserId(), payload)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(TestDataLoader.knownUserId()))
                .body("name", equalTo(payload.get("name")))
                .body("username", equalTo(payload.get("username")))
                .body("email", equalTo(payload.get("email")));
    }

    @Test
    public void updateUserWithPatch_returns200WithPatchedField() {
        Map<String, Object> payload = TestDataLoader.userPayload("patchUser");
        usersClient.patchUser(TestDataLoader.knownUserId(), payload)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(TestDataLoader.knownUserId()))
                .body("name", equalTo(payload.get("name")));
    }

    @Test
    public void deleteUser_returns200() {
        usersClient.deleteUser(TestDataLoader.knownUserId())
        .then()
                .statusCode(200)
                .body(equalTo("{}"));
    }

    @Test
    public void getCommentsForPost_returnsOnlyThatPostsComments() {
        int postId = TestDataLoader.knownPostIdForComments();
        usersClient.getCommentsForPost(postId)
        .then()
                .spec(okJsonResponse())
                .body("size()", equalTo(TestDataLoader.expectedCommentsForPost(postId)))
                .body("postId", everyItem(equalTo(postId)))
                .body("[0].id", equalTo(TestDataLoader.knownCommentId()))
                .body("[0].name", equalTo(TestDataLoader.knownCommentName()))
                .body("email", everyItem(not(emptyOrNullString())))
                .body("body", everyItem(not(emptyOrNullString())));
    }

    @Test
    public void getCommentById_returnsSingleComment() {
        int commentId = TestDataLoader.knownCommentId();
        usersClient.getCommentById(commentId)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(commentId))
                .body("postId", equalTo(TestDataLoader.knownCommentPostId()))
                .body("name", equalTo(TestDataLoader.knownCommentName()))
                .body("email", equalTo(TestDataLoader.knownCommentEmail()))
                .body("body", not(emptyOrNullString()));
    }
}
