package com.mcsolo.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper around the JSONPlaceholder {@code /posts} endpoints.
 * Holds no assertions and no test data; it only performs the HTTP calls
 * so the test classes read as "do this, then check that".
 */
public class PostsClient {

    private final RequestSpecification spec;

    public PostsClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response getAllPosts() {
        return given().spec(spec).when().get("/posts");
    }

    public Response getPostsByUser(int userId) {
        return given().spec(spec).queryParam("userId", userId).when().get("/posts");
    }

    public Response getPostById(Object id) {
        return given().spec(spec).when().get("/posts/" + id);
    }

    public Response getPostByIdWithHeader(Object id, String headerName, String headerValue) {
        return given().spec(spec).header(headerName, headerValue).when().get("/posts/" + id);
    }

    public Response getRoute(String route) {
        return given().spec(spec).when().get(route);
    }

    public Response createPost(Map<String, Object> payload) {
        return given().spec(spec).body(payload).when().post("/posts");
    }

    public Response updatePost(Object id, Map<String, Object> payload) {
        return given().spec(spec).body(payload).when().put("/posts/" + id);
    }

    public Response patchPost(Object id, Map<String, Object> payload) {
        return given().spec(spec).body(payload).when().patch("/posts/" + id);
    }

    public Response deletePost(Object id) {
        return given().spec(spec).when().delete("/posts/" + id);
    }
}
