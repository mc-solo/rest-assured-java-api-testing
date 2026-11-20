package com.mcsolo.tests;

import com.mcsolo.clients.AuthClient;
import com.mcsolo.clients.PostsClient;
import com.mcsolo.config.TestConfig;
import com.mcsolo.helpers.TestDataLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.params.CoreConnectionPNames;
import org.testng.annotations.BeforeClass;

/**
 * Shared setup for all API tests.
 * Base URI is JSONPlaceholder, a free fake REST API.
 * Auth tests use httpbin.org, which has real basic-auth and bearer endpoints.
 *
 * <p>All configuration comes from {@link TestConfig}: env var, then
 * {@code -D} system property, then {@code .env}, then
 * {@code config/{env}.properties}, then {@code config/config.properties},
 * then a hard-coded fallback default. The only URLs ever hard-coded are the
 * two public demo APIs, as fallback defaults inside {@code TestConfig}.</p>
 */
public class BaseTest {

    /**
     * Base URI for JSONPlaceholder, from {@link TestConfig#apiBaseUri()}.
     * Override with {@code API_BASE_URL} or {@code -Dapi.baseUri=...}.
     */
    protected static final String BASE_URI = TestConfig.apiBaseUri();

    /**
     * Base URI for httpbin, from {@link TestConfig#httpbinBaseUri()}.
     * Override with {@code HTTPBIN_BASE_URL} or {@code -Dhttpbin.baseUri=...}.
     */
    protected static final String HTTPBIN_URI = TestConfig.httpbinBaseUri();

    protected RequestSpecification requestSpec;
    protected PostsClient postsClient;
    protected AuthClient authClient;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, TestConfig.connectTimeoutMs())
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, TestConfig.readTimeoutMs()));
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
        postsClient = new PostsClient(requestSpec);
        authClient = new AuthClient(HTTPBIN_URI,
                TestDataLoader.basicAuthUsername("valid"),
                TestDataLoader.basicAuthPassword("valid"));
    }

    /** Response spec for happy-path JSONPlaceholder calls: 200 + JSON body. */
    protected ResponseSpecification okJsonResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
