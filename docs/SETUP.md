# Setup

## Prerequisites

- Java 17 or newer (`java -version`)
- Maven 3.8 or newer (`mvn -version`)
- Internet access: the suite calls JSONPlaceholder and httpbin, and Maven
  downloads dependencies from Maven Central on the first run.

## Install

```bash
git clone <repo-url>
cd rest-assured-java-api-testing
mvn -o validate   # offline check that the POM is valid (no downloads)
```

No other install step is needed. There is no local server and no test
data setup: the tests run against the public demo APIs.

## Configuration (optional)

With no configuration the suite runs against the defaults in
`src/test/resources/config/config.properties`:

- API: `https://jsonplaceholder.typicode.com`
- Auth API: `https://httpbin.org`
- Connect timeout 5000 ms, read timeout 10000 ms

To override, pick one (highest precedence first):

```bash
# 1. Environment variables
export API_BASE_URL=http://localhost:18080
export HTTPBIN_BASE_URL=http://localhost:18080
export TEST_ENV=dev

# 2. JVM system properties
mvn test -Dapi.baseUri=http://localhost:18080 -Dhttpbin.baseUri=http://localhost:18080

# 3. A .env file in the project root (copy the template, never commit it)
cp .env.example .env
# then edit .env

# 4. Edit src/test/resources/config/dev.properties / prod.properties
```

Full key list (env var / system property / properties key):

| Setting | Env var | System property / properties key | Default |
| --- | --- | --- | --- |
| Environment | `TEST_ENV` | `test.env` | `dev` |
| API base URI | `API_BASE_URL` | `api.baseUri` | `https://jsonplaceholder.typicode.com` |
| Auth API base URI | `HTTPBIN_BASE_URL` | `httpbin.baseUri` | `https://httpbin.org` |
| Connect timeout (ms) | `API_CONNECT_TIMEOUT_MS` | `api.connectTimeoutMs` | `5000` |
| Read timeout (ms) | `API_READ_TIMEOUT_MS` | `api.readTimeoutMs` | `10000` |
| Request timeout (ms) | `API_REQUEST_TIMEOUT_MS` | `api.requestTimeoutMs` | `10000` |
| Basic-auth username | `AUTH_BASIC_USERNAME` | `auth.basic.username` | `user` (dummy demo value) |
| Basic-auth password | `AUTH_BASIC_PASSWORD` | `auth.basic.password` | `passwd` (dummy demo value) |
| Bearer token | `AUTH_BEARER_TOKEN` | `auth.bearer.token` | `sometoken123` (dummy demo value) |

The auth values are dummy credentials for httpbin's public demo
endpoints, not real accounts. Never put real credentials in the
properties files; use env vars or `.env` (which is git-ignored).

## First run

```bash
mvn test
```

See `docs/TESTING.md` for what runs and where the reports go.
