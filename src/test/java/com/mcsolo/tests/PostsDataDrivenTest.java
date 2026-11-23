package com.mcsolo.tests;

import com.mcsolo.helpers.TestDataLoader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Data-driven coverage for the /posts resource on JSONPlaceholder.
 * Every row comes from test-data/data-driven-posts.json via
 * TestDataLoader.dataDrivenPostRows(); the test class holds no
 * payload, id or expected value of its own. Each row drives two
 * tests (create + get by id), so 3 rows make 6 tests.
 * Note: JSONPlaceholder is a fake API, it accepts writes and echoes
 * them back, but nothing is really saved on the server.
 */
public class PostsDataDrivenTest extends BaseTest {

    @DataProvider(name = "postRows")
    public Object[][] postRows() {
        List<Map<String, Object>> rows = TestDataLoader.dataDrivenPostRows();
        return rows.stream()
                .map(row -> new Object[]{row})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "postRows")
    public void createPost_dataDriven_returnsExpectedStatusWithEchoedBody(Map<String, Object> row) {
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) row.get("payload");
        int expectedStatus = ((Number) row.get("expectedStatus")).intValue();
        postsClient.createPost(payload)
        .then()
                .statusCode(expectedStatus)
                .contentType("application/json; charset=utf-8")
                .body("title", equalTo(payload.get("title")))
                .body("body", equalTo(payload.get("body")))
                .body("userId", equalTo(payload.get("userId")))
                .body("id", notNullValue());
    }

    @Test(dataProvider = "postRows")
    public void getPostById_dataDriven_returnsKnownPost(Map<String, Object> row) {
        int postId = ((Number) row.get("knownPostId")).intValue();
        int expectedUserId = ((Number) row.get("expectedUserId")).intValue();
        postsClient.getPostById(postId)
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(postId))
                .body("userId", equalTo(expectedUserId))
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }
}
