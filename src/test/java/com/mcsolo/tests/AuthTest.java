package com.mcsolo.tests;

import com.mcsolo.helpers.TestDataLoader;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

/**
 * Auth checks against httpbin.org, which has real auth endpoints.
 * JSONPlaceholder itself has no auth, so the header test there only
 * proves a custom header can be sent without breaking the call.
 * Credentials and tokens live in test-data/auth.json and are dummy values
 * for httpbin's public demo endpoints, never real accounts.
 */
public class AuthTest extends BaseTest {

    @Test
    public void basicAuth_withValidCredentials_returns200() {
        String username = TestDataLoader.basicAuthUsername("valid");
        String password = TestDataLoader.basicAuthPassword("valid");
        authClient.basicAuth(username, password)
        .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("user", equalTo(username));
    }

    @Test
    public void basicAuth_withWrongPassword_returns401() {
        authClient.basicAuth(
                TestDataLoader.basicAuthUsername("wrongPassword"),
                TestDataLoader.basicAuthPassword("wrongPassword"))
        .then()
                .statusCode(401);
    }

    @Test
    public void basicAuth_withWrongUsername_returns401() {
        authClient.basicAuth(
                TestDataLoader.basicAuthUsername("wrongUsername"),
                TestDataLoader.basicAuthPassword("wrongUsername"))
        .then()
                .statusCode(401);
    }

    @Test
    public void basicAuth_withNoCredentials_returns401() {
        authClient.basicAuthWithoutCredentials()
        .then()
                .statusCode(401);
    }

    @Test
    public void bearerAuth_withValidToken_returns200() {
        String token = TestDataLoader.bearerToken();
        authClient.bearerAuth(token)
        .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("token", equalTo(token));
    }

    @Test
    public void bearerAuth_withNoToken_returns401() {
        authClient.bearerWithoutToken()
        .then()
                .statusCode(401);
    }

    @Test
    public void customHeader_sentToJsonPlaceholder_doesNotBreakCall() {
        postsClient.getPostByIdWithHeader(
                TestDataLoader.knownPostId(),
                TestDataLoader.customHeaderName(),
                TestDataLoader.customHeaderValue())
        .then()
                .spec(okJsonResponse())
                .body("id", equalTo(TestDataLoader.knownPostId()));
    }
}
