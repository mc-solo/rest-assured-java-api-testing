package com.mcsolo.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared setup for all API tests.
 * Base URI is JSONPlaceholder, a free fake REST API.
 * Auth tests use httpbin.org, which has real basic-auth and bearer endpoints.
 */
public class BaseTest {

    /**
     * Base URIs, overridable with system properties so the suite can run
     * against a local stub, for example:
     * mvn test -Dapi.baseUri=http://localhost:18080 -Dhttpbin.baseUri=http://localhost:18080
     */
    protected static final String BASE_URI =
            System.getProperty("api.baseUri", "https://jsonplaceholder.typicode.com");
    protected static final String HTTPBIN_URI =
            System.getProperty("httpbin.baseUri", "https://httpbin.org");

    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /** Response spec for happy-path JSONPlaceholder calls: 200 + JSON body. */
    protected ResponseSpecification okJsonResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    /** Small helper to build a post payload without a model class. */
    protected Map<String, Object> postPayload(String title, String body, int userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("body", body);
        payload.put("userId", userId);
        return payload;
    }
}
