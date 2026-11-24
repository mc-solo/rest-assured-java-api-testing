# Testing

## What is tested

All tests run against public demo APIs. JSONPlaceholder is a fake API:
writes (POST, PUT, PATCH, DELETE) return the right status and echo the
data, but nothing is really saved.

| Class | Tests | Covers |
| --- | --- | --- |
| `PostsCrudTest` | 7 | GET all posts (100), GET filtered by user (10 for user 1), GET one post by id, POST (201 + echo), PUT, PATCH, DELETE (200 + `{}`) |
| `PostsDataDrivenTest` | 6 | TestNG data provider over 3 rows from `test-data/data-driven-posts.json`: create post (201 + echo) and get post by id, per row |
| `UsersCrudTest` | 8 | GET all users (10), GET one user by id, POST (201 + echo), PUT, PATCH, DELETE (200 + `{}`) on `/users`; GET comments for a post (5 for post 1) and GET one comment by id |
| `AuthTest` | 7 | Basic auth valid / wrong password / wrong username / missing (401s) against httpbin `/basic-auth`, bearer token valid / missing against httpbin `/bearer`, custom header sent to JSONPlaceholder |
| `NegativeAndSchemaTest` | 6 | 404 for unknown id, non-numeric id (`{}` body), unknown route; JSON schema validation for a single post and a created post (`schemas/post-schema.json`); required fields on every item in the full list |
| **Total** | **34** | |

Test data (payloads, ids, dummy credentials, expected counts) lives in
`src/test/resources/test-data/` (`posts.json`, `users.json`,
`users-crud.json`, `auth.json`, `data-driven-posts.json`) and is loaded by
`TestDataLoader`. No payloads or credentials are hard-coded in the test
classes. To add a data-driven row, add an object to the `rows` array in
`data-driven-posts.json` — no code change is needed.

## How to run

```bash
mvn test                                  # full suite (src/test/resources/testng.xml)
mvn -Dtest=PostsCrudTest test             # one class only
mvn -Dtest=UsersCrudTest test             # the users + comments class only
mvn -Dtest=PostsCrudTest#getAllPosts_returnsList test   # one method only
```

Run against a local stub instead of the public APIs:

```bash
mvn test -Dapi.baseUri=http://localhost:18080 -Dhttpbin.baseUri=http://localhost:18080
```

Or pick an environment:

```bash
TEST_ENV=prod mvn test
```

## Reports

- Surefire reports: `target/surefire-reports/` (text + XML per class)
- TestNG reports: `target/surefire-reports/testng-results.xml`
  (and `test-output/` if TestNG is run directly)

## Honest status

`mvn test` has **never** been run end to end in this sandbox: the sandbox
blocks direct TCP connections from Java processes, so Maven cannot
download dependencies and the tests cannot reach the internet from the
JVM. What has been verified here:

- `mvn -o validate` — BUILD SUCCESS (POM valid, offline).
- The test sources compile — checked with `javac` against minimal
  hand-written stubs of the Rest Assured / TestNG / Jackson / Hamcrest
  APIs, because the real jars are not on this machine and cannot be
  downloaded here. This checks syntax and API usage, not runtime behaviour.
- Every expected status code, body and count in the tests was checked
  against the live APIs with curl first (GET list is 100 posts,
  `userId=1` returns 10, POST returns 201, PUT/PATCH return 200, DELETE
  returns 200, unknown ids and routes return 404, httpbin basic auth
  returns 401 without or with wrong credentials and 200 with the demo
  `user` / `passwd`; GET users is 10 users, user 1 is Leanne Graham /
  Bret / Sincere@april.biz, POST /users returns 201 with id 11, PUT/PATCH
  /users/1 return 200, DELETE /users/1 returns 200 with `{}`, post 1 has
  5 comments, comment 1 is `id labore ex et quam laborum` from
  Eliseo@gardner.biz).

Run `mvn test` on your own machine (or any machine with normal internet)
for the first real run, then record the result in the README.
