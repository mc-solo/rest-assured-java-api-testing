package com.mcsolo.tests;

import com.mcsolo.helpers.TestDataLoader;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/**
 * Negative cases and response-shape checks.
 * JSONPlaceholder returns 404 for unknown ids and unknown routes,
 * and an empty object {} for a non-numeric id like /posts/abc.
 * Ids, routes and payloads live in test-data/users.json and
 * test-data/posts.json and are loaded via TestDataLoader.
 */
public class NegativeAndSchemaTest extends BaseTest {

    @Test
    public void getPostWithUnknownId_returns404() {
        postsClient.getPostById(TestDataLoader.unknownPostId())
        .then()
                .statusCode(404);
    }

    @Test
    public void getPostWithNonNumericId_returns404WithEmptyBody() {
        postsClient.getPostById(TestDataLoader.nonNumericPostId())
        .then()
                .statusCode(404)
                .body(equalTo("{}"));
    }

    @Test
    public void getUnknownRoute_returns404() {
        postsClient.getRoute(TestDataLoader.unknownRoute())
        .then()
                .statusCode(404);
    }

    @Test
    public void getSinglePost_matchesJsonSchema() {
        postsClient.getPostById(TestDataLoader.knownPostId())
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @Test
    public void createPost_matchesJsonSchema() {
        postsClient.createPost(TestDataLoader.postPayload("schemaCheckPost"))
        .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @Test
    public void getAllPosts_everyItemHasRequiredFields() {
        postsClient.getAllPosts()
        .then()
                .statusCode(200)
                .body("userId", everyItem(notNullValue()))
                .body("id", everyItem(notNullValue()))
                .body("title", everyItem(not(emptyOrNullString())))
                .body("body", everyItem(not(emptyOrNullString())));
    }
}
