# rest-assured-java-api-testing

API tests in Java with Rest Assured and TestNG, run against
[JSONPlaceholder](https://jsonplaceholder.typicode.com) (a free fake REST API)
and [httpbin.org](https://httpbin.org) for the auth checks.

Java 17, Maven, TestNG. No real server or test data setup needed.

## What is covered

- `PostsCrudTest` — GET all posts, GET posts filtered by user, GET one post
  by id, POST, PUT, PATCH and DELETE on `/posts`.
- `AuthTest` — basic auth with good, wrong and missing credentials, bearer
  token with and without a token, plus a custom header sent to JSONPlaceholder.
  JSONPlaceholder has no auth of its own, so the real auth checks run
  against httpbin's `/basic-auth` and `/bearer` endpoints.
- `NegativeAndSchemaTest` — 404 for an unknown id, a non-numeric id and an
  unknown route, JSON schema validation for a single post and a created post
  (`schemas/post-schema.json`), and required-field checks on the full list.

Note: JSONPlaceholder is a fake API. Writes (POST, PUT, PATCH, DELETE)
return the right status and echo the data, but nothing is really saved.

## How to run

```bash
mvn test
```

That runs the suite in `src/test/resources/testng.xml`.
To run one class only:

```bash
mvn -Dtest=PostsCrudTest test
```

Needs internet access to reach JSONPlaceholder and httpbin, and to
download dependencies from Maven Central on the first run.

## Test results

20 tests in 3 classes (7 in `PostsCrudTest`, 7 in `AuthTest`, 6 in
`NegativeAndSchemaTest`).

Honest status: the tests have **not** been run end to end against the live
APIs from this machine yet. The test code compiles clean against the real
Rest Assured 5.5.0 / TestNG 7.10.2 jars, and every expected status code,
body and count in the tests was checked against the live APIs with curl
first (GET list is 100 posts, `userId=1` returns 10, POST returns 201,
PUT/PATCH return 200, DELETE returns 200, unknown ids and routes return
404, httpbin basic auth returns 401 without or with wrong credentials and
200 with `user` / `passwd`). But `mvn test` could not run here: this
sandbox blocks direct TCP connections from Java processes, so Maven
cannot download dependencies and the tests cannot reach the internet
from the JVM. Run `mvn test` on your own machine (or any machine with
normal internet) for the first real run, then record the result here.
