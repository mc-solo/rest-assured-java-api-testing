package com.mcsolo.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper around the JSONPlaceholder {@code /users} and
 * {@code /comments} endpoints. Holds no assertions and no test data;
 * it only performs the HTTP calls so the test classes read as
 * "do this, then check that".
 */
public class UsersClient {

    private final RequestSpecification spec;

    public UsersClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response getAllUsers() {
        return given().spec(spec).when().get("/users");
    }

    public Response getUserById(Object id) {
        return given().spec(spec).when().get("/users/" + id);
    }

    public Response createUser(Map<String, Object> payload) {
        return given().spec(spec).body(payload).when().post("/users");
    }

    public Response updateUser(Object id, Map<String, Object> payload) {
        return given().spec(spec).body(payload).when().put("/users/" + id);
    }

    public Response patchUser(Object id, Map<String, Object> payload) {
        return given().spec(spec).body(payload).when().patch("/users/" + id);
    }

    public Response deleteUser(Object id) {
        return given().spec(spec).when().delete("/users/" + id);
    }

    public Response getCommentsForPost(Object postId) {
        return given().spec(spec).when().get("/posts/" + postId + "/comments");
    }

    public Response getCommentById(Object id) {
        return given().spec(spec).when().get("/comments/" + id);
    }
}
