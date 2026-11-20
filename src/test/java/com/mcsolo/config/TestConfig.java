package com.mcsolo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration for the test suite.
 *
 * <p>Lookup precedence, highest first:</p>
 * <ol>
 *   <li>Environment variable, e.g. {@code API_BASE_URL}</li>
 *   <li>JVM system property, e.g. {@code -Dapi.baseUri=http://localhost:18080}</li>
 *   <li>{@code .env} file in the project root (local dev convenience, never committed)</li>
 *   <li>{@code src/test/resources/config/{env}.properties}, where {@code env} is
 *       {@code TEST_ENV} / {@code test.env} (default {@code dev})</li>
 *   <li>{@code src/test/resources/config/config.properties} (committed defaults)</li>
 *   <li>Hard-coded fallback defaults below. These exist only so the suite runs
 *       with zero setup against the public demo APIs. The only URLs ever
 *       hard-coded here are the two public demo APIs; there are no real
 *       service URLs or real credentials anywhere in this codebase.</li>
 * </ol>
 */
public final class TestConfig {

    /** Fallback default: public fake REST API used by the posts tests. */
    public static final String DEFAULT_API_BASE_URI = "https://jsonplaceholder.typicode.com";
    /** Fallback default: public demo API used by the auth tests. */
    public static final String DEFAULT_HTTPBIN_BASE_URI = "https://httpbin.org";
    public static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
    public static final int DEFAULT_READ_TIMEOUT_MS = 10000;
    public static final int DEFAULT_REQUEST_TIMEOUT_MS = 10000;
    /** Dummy demo credentials for httpbin's public /basic-auth endpoint. Not a real account. */
    public static final String DEFAULT_BASIC_USERNAME = "user";
    /** Dummy demo password for httpbin's public /basic-auth endpoint. Not a real password. */
    public static final String DEFAULT_BASIC_PASSWORD = "passwd";
    /** Dummy token echoed back by httpbin's public /bearer endpoint. Not a real token. */
    public static final String DEFAULT_BEARER_TOKEN = "sometoken123";

    private static final Properties FILE_PROPS = loadFileProperties();
    private static final Properties DOTENV_PROPS = loadDotEnv();

    private TestConfig() {
    }

    /** Active environment name: {@code TEST_ENV} env var, else {@code test.env} system property, else {@code dev}. */
    public static String testEnv() {
        return firstNonBlank(System.getenv("TEST_ENV"), System.getProperty("test.env"), "dev");
    }

    /** Base URI for JSONPlaceholder (the posts API). */
    public static String apiBaseUri() {
        return resolve("API_BASE_URL", "api.baseUri", DEFAULT_API_BASE_URI);
    }

    /** Base URI for httpbin (the auth endpoints). */
    public static String httpbinBaseUri() {
        return resolve("HTTPBIN_BASE_URL", "httpbin.baseUri", DEFAULT_HTTPBIN_BASE_URI);
    }

    public static int connectTimeoutMs() {
        return resolveInt("API_CONNECT_TIMEOUT_MS", "api.connectTimeoutMs", DEFAULT_CONNECT_TIMEOUT_MS);
    }

    public static int readTimeoutMs() {
        return resolveInt("API_READ_TIMEOUT_MS", "api.readTimeoutMs", DEFAULT_READ_TIMEOUT_MS);
    }

    public static int requestTimeoutMs() {
        return resolveInt("API_REQUEST_TIMEOUT_MS", "api.requestTimeoutMs", DEFAULT_REQUEST_TIMEOUT_MS);
    }

    /** Dummy demo username for httpbin basic auth. Override with {@code AUTH_BASIC_USERNAME} if needed. */
    public static String basicAuthUsername() {
        return resolve("AUTH_BASIC_USERNAME", "auth.basic.username", DEFAULT_BASIC_USERNAME);
    }

    /** Dummy demo password for httpbin basic auth. Override with {@code AUTH_BASIC_PASSWORD} if needed. */
    public static String basicAuthPassword() {
        return resolve("AUTH_BASIC_PASSWORD", "auth.basic.password", DEFAULT_BASIC_PASSWORD);
    }

    /** Dummy bearer token for httpbin. Override with {@code AUTH_BEARER_TOKEN} if needed. */
    public static String bearerToken() {
        return resolve("AUTH_BEARER_TOKEN", "auth.bearer.token", DEFAULT_BEARER_TOKEN);
    }

    /** Generic lookup with the same precedence; {@code fallback} is the last resort. */
    public static String get(String envName, String key, String fallback) {
        return resolve(envName, key, fallback);
    }

    private static String resolve(String envName, String key, String fallback) {
        // .env files use env-var style names (API_BASE_URL); properties files
        // use dotted keys (api.baseUri). Accept either spelling in .env.
        return firstNonBlank(
                System.getenv(envName),
                System.getProperty(key),
                DOTENV_PROPS.getProperty(envName),
                DOTENV_PROPS.getProperty(key),
                FILE_PROPS.getProperty(key),
                fallback);
    }

    private static int resolveInt(String envName, String key, int fallback) {
        String raw = resolve(envName, key, null);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            System.err.println("[TestConfig] Invalid integer for " + key + ": '" + raw + "', using fallback " + fallback);
            return fallback;
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    /** Loads config.properties, then config/{env}.properties on top of it. Missing files are skipped. */
    private static Properties loadFileProperties() {
        Properties props = new Properties();
        loadFromClasspath(props, "config/config.properties");
        loadFromClasspath(props, "config/" + testEnv() + ".properties");
        return props;
    }

    private static void loadFromClasspath(Properties props, String classpathPath) {
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream(classpathPath)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("[TestConfig] Could not read " + classpathPath + ": " + e.getMessage());
        }
    }

    /**
     * Minimal {@code .env} parser (KEY=VALUE lines, {@code #} comments, optional quotes).
     * Only read from the project root; a missing file is normal and means "no local overrides".
     */
    private static Properties loadDotEnv() {
        Properties props = new Properties();
        File dotEnv = new File(System.getProperty("user.dir"), ".env");
        if (!dotEnv.isFile()) {
            return props;
        }
        try (FileInputStream in = new FileInputStream(dotEnv)) {
            Properties raw = new Properties();
            raw.load(in);
            raw.forEach((key, value) -> props.setProperty(
                    String.valueOf(key).trim(), unquote(String.valueOf(value).trim())));
        } catch (IOException e) {
            System.err.println("[TestConfig] Could not read .env: " + e.getMessage());
        }
        return props;
    }

    private static String unquote(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
