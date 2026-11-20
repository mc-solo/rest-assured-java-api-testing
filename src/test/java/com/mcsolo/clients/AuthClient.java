package com.mcsolo.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper around httpbin.org's auth endpoints.
 * JSONPlaceholder itself has no auth, so the real auth checks run here.
 *
 * <p>{@code expectedUsername} / {@code expectedPassword} are the credentials
 * the {@code /basic-auth/{user}/{passwd}} URL itself expects (httpbin's
 * public demo user). The {@code username} / {@code password} arguments are
 * the credentials actually sent, so a test can send wrong ones on purpose
 * and still hit the same URL.</p>
 */
public class AuthClient {

    private final String httpbinUri;
    private final String expectedUsername;
    private final String expectedPassword;

    public AuthClient(String httpbinUri, String expectedUsername, String expectedPassword) {
        this.httpbinUri = httpbinUri;
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
    }

    public Response basicAuth(String username, String password) {
        return given()
                .baseUri(httpbinUri)
                .auth().basic(username, password)
                .when()
                .get("/basic-auth/" + expectedUsername + "/" + expectedPassword);
    }

    public Response basicAuthWithoutCredentials() {
        return given()
                .baseUri(httpbinUri)
                .when()
                .get("/basic-auth/" + expectedUsername + "/" + expectedPassword);
    }

    public Response bearerAuth(String token) {
        return given()
                .baseUri(httpbinUri)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/bearer");
    }

    public Response bearerWithoutToken() {
        return given()
                .baseUri(httpbinUri)
                .when()
                .get("/bearer");
    }
}
