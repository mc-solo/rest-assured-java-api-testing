package com.mcsolo.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads JSON test data from {@code src/test/resources/test-data/}.
 * This is the canonical (and only) location for test data in this repo.
 * Tests never hard-code payloads, ids or credentials; they ask this class.
 * All credentials in the data files are dummy values for the public demo
 * APIs (httpbin's documented demo user, a made-up bearer token), never real.
 */
public final class TestDataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, JsonNode> CACHE = new HashMap<>();

    private static final String POSTS_FILE = "test-data/posts.json";
    private static final String USERS_FILE = "test-data/users.json";
    private static final String AUTH_FILE = "test-data/auth.json";

    private TestDataLoader() {
    }

    /** Loads (and caches) a JSON file from the test classpath, e.g. {@code "test-data/posts.json"}. */
    public static JsonNode load(String classpathPath) {
        JsonNode cached = CACHE.get(classpathPath);
        if (cached != null) {
            return cached;
        }
        try (InputStream in = TestDataLoader.class.getClassLoader().getResourceAsStream(classpathPath)) {
            if (in == null) {
                throw new IllegalStateException("Test data file not found on classpath: " + classpathPath);
            }
            JsonNode node = MAPPER.readTree(in);
            CACHE.put(classpathPath, node);
            return node;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read test data file: " + classpathPath, e);
        }
    }

    // ---- posts (test-data/posts.json) ----

    /** A post payload from {@code payloads.<key>} in posts.json, as a map Rest Assured can send as JSON. */
    public static Map<String, Object> postPayload(String key) {
        JsonNode node = load(POSTS_FILE).path("payloads").path(key);
        if (node.isMissingNode()) {
            throw new IllegalArgumentException("No post payload named '" + key + "' in " + POSTS_FILE);
        }
        return MAPPER.convertValue(node, new TypeReference<Map<String, Object>>() {
        });
    }

    /** Expected total number of posts returned by GET /posts. */
    public static int expectedTotalPosts() {
        return requiredInt(load(POSTS_FILE).path("expected").path("totalPosts"), "expected.totalPosts");
    }

    // ---- users / ids (test-data/users.json) ----

    /** Id of the known post used across tests (JSONPlaceholder post 1). */
    public static int knownPostId() {
        return requiredInt(load(USERS_FILE).path("knownPost").path("id"), "knownPost.id");
    }

    /** Owner of the known post. */
    public static int knownPostUserId() {
        return requiredInt(load(USERS_FILE).path("knownPost").path("userId"), "knownPost.userId");
    }

    /** userId used for the "filter posts by user" test. */
    public static int filterUserId() {
        return requiredInt(load(USERS_FILE).path("filter").path("userId"), "filter.userId");
    }

    /** Expected number of posts for a user, from the {@code users} array in users.json. */
    public static int expectedPostsForUser(int userId) {
        for (JsonNode user : load(USERS_FILE).path("users")) {
            if (user.path("id").asInt() == userId) {
                return requiredInt(user.path("expectedPostCount"), "users[].expectedPostCount");
            }
        }
        throw new IllegalArgumentException("No expected post count for userId " + userId + " in " + USERS_FILE);
    }

    /** An id that does not exist on the API, for the 404 tests. */
    public static int unknownPostId() {
        return requiredInt(load(USERS_FILE).path("unknownPostId"), "unknownPostId");
    }

    /** A non-numeric id, for the 404-with-empty-body test. */
    public static String nonNumericPostId() {
        return requiredText(load(USERS_FILE).path("nonNumericPostId"), "nonNumericPostId");
    }

    /** A route that does not exist on the API, for the 404 test. */
    public static String unknownRoute() {
        return requiredText(load(USERS_FILE).path("unknownRoute"), "unknownRoute");
    }

    // ---- auth (test-data/auth.json, dummy demo credentials only) ----

    /**
     * Basic-auth username for a scenario: {@code "valid"}, {@code "wrongPassword"} or {@code "wrongUsername"}.
     */
    public static String basicAuthUsername(String scenario) {
        return requiredText(load(AUTH_FILE).path("basicAuth").path(scenario).path("username"),
                "basicAuth." + scenario + ".username");
    }

    /**
     * Basic-auth password for a scenario: {@code "valid"}, {@code "wrongPassword"} or {@code "wrongUsername"}.
     */
    public static String basicAuthPassword(String scenario) {
        return requiredText(load(AUTH_FILE).path("basicAuth").path(scenario).path("password"),
                "basicAuth." + scenario + ".password");
    }

    /** Dummy bearer token echoed back by httpbin's /bearer endpoint. */
    public static String bearerToken() {
        return requiredText(load(AUTH_FILE).path("bearer").path("token"), "bearer.token");
    }

    /** Name of the custom header sent in the header test. */
    public static String customHeaderName() {
        return requiredText(load(AUTH_FILE).path("customHeader").path("name"), "customHeader.name");
    }

    /** Value of the custom header sent in the header test. */
    public static String customHeaderValue() {
        return requiredText(load(AUTH_FILE).path("customHeader").path("value"), "customHeader.value");
    }

    private static int requiredInt(JsonNode node, String what) {
        if (node == null || node.isMissingNode() || !node.isNumber()) {
            throw new IllegalStateException("Missing or non-numeric test data value: " + what);
        }
        return node.asInt();
    }

    private static String requiredText(JsonNode node, String what) {
        if (node == null || node.isMissingNode() || node.asText().isBlank()) {
            throw new IllegalStateException("Missing or blank test data value: " + what);
        }
        return node.asText();
    }
}
