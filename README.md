# rest-assured-java-api-testing

API tests in Java with Rest Assured and TestNG, run against
[JSONPlaceholder](https://jsonplaceholder.typicode.com) (a free fake REST API)
and [httpbin.org](https://httpbin.org) for the auth checks.

Java 17, Maven, TestNG. No real server or test data setup needed.

## Folder tree

```text
├── .env.example                  # local env vars template (copy to .env, never commit .env)
├── ARCHITECTURE.md               # folder tree, data flow, how to add a test
├── docs/
│   ├── SETUP.md                  # install Java/Maven, first run, config keys
│   └── TESTING.md                # what is tested, how to run, reports
├── pom.xml
└── src/test/
    ├── java/com/mcsolo/
    │   ├── config/TestConfig.java      # env var > -D > .env > config/{env}.properties > defaults
    │   ├── clients/PostsClient.java    # /posts calls
    │   ├── clients/AuthClient.java     # httpbin auth calls
    │   ├── helpers/TestDataLoader.java # loads JSON test data
    │   └── tests/                      # BaseTest, PostsCrudTest, AuthTest, NegativeAndSchemaTest
    └── resources/
        ├── config/                     # config.properties, dev.properties, prod.properties
        ├── schemas/post-schema.json
        ├── test-data/                  # posts.json, users.json, auth.json
        └── testng.xml
```

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

Test data (payloads, ids, dummy credentials, expected counts) lives in
`src/test/resources/test-data/` and is loaded by `TestDataLoader`, not
hard-coded in the tests. Configuration (base URIs, timeouts) lives in
`TestConfig` and `src/test/resources/config/`; see `docs/SETUP.md`.

Note: JSONPlaceholder is a fake API. Writes (POST, PUT, PATCH, DELETE)
return the right status and echo the data, but nothing is really saved.

## Setup

Requires Java 17+ and Maven 3.8+. Full steps in `docs/SETUP.md`.

```bash
mvn -o validate   # offline check that the POM is valid
```

## How to run

```bash
mvn test
```

That runs the suite in `src/test/resources/testng.xml`.
To run one class only:

```bash
mvn -Dtest=PostsCrudTest test
```

To run against different base URIs (for example a local stub):

```bash
mvn test -Dapi.baseUri=http://localhost:18080 -Dhttpbin.baseUri=http://localhost:18080
```

Needs internet access to reach JSONPlaceholder and httpbin, and to
download dependencies from Maven Central on the first run.

## Test results

20 tests in 3 classes (7 in `PostsCrudTest`, 7 in `AuthTest`, 6 in
`NegativeAndSchemaTest`).

Honest status: `mvn test` has **never** been run. What was verified in
this sandbox is `mvn -o validate` — BUILD SUCCESS — and a `javac`
compile check of the test sources against minimal hand-written stubs of
the Rest Assured / TestNG / Jackson / Hamcrest APIs (the real jars are
not on this machine and cannot be downloaded here, so this checks syntax
and API usage, not runtime behaviour). The expected status codes, bodies
and counts in the tests were checked against the live APIs with curl
first (GET list is 100 posts, `userId=1` returns 10, POST returns 201,
PUT/PATCH return 200, DELETE returns 200, unknown ids and routes return
404, httpbin basic auth returns 401 without or with wrong credentials and
200 with the demo `user` / `passwd`). But `mvn test` could not run here:
this sandbox blocks direct TCP connections from Java processes, so Maven
cannot download dependencies and the tests cannot reach the internet
from the JVM. Run `mvn test` on your own machine (or any machine with
normal internet) for the first real run, then record the result here.
