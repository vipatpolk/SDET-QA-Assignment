package com.atait.assignment.api.client;

import com.atait.assignment.api.models.UserRequest;
import com.atait.assignment.config.ConfigurationManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;

/**
 * Reusable Service Layer Client for GoREST Users API.
 * Encapsulates endpoint URLs, authentication logic, request/response logging, and error handling.
 */
public class GoRestClient {

    private static final int MAX_RETRIES = 3;
    private static final long DEFAULT_BACKOFF_MS = 1500L;

    private static volatile GoRestClient instance;
    private final String baseUrl;

    private GoRestClient() {
        this.baseUrl = ConfigurationManager.getGoRestBaseUrl();
    }

    public static GoRestClient getInstance() {
        if (instance == null) {
            synchronized (GoRestClient.class) {
                if (instance == null) {
                    instance = new GoRestClient();
                }
            }
        }
        return instance;
    }

    private RequestSpecification buildBaseSpec(String tokenMode) {
        // Redact Authorization header to protect live API credentials from test logs
        LogConfig logConfig = LogConfig.logConfig().blacklistHeader("Authorization");
        RestAssuredConfig config = RestAssuredConfig.config().logConfig(logConfig);

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath("/users")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(config)
                .log(LogDetail.ALL); // Dumps request method, URI, params, and body; Authorization is masked

        if ("omitted".equalsIgnoreCase(tokenMode)) {
            // Do not attach Authorization header
        } else if ("invalid".equalsIgnoreCase(tokenMode)) {
            builder.addHeader("Authorization", "Bearer invalid-token-xyz-123");
        } else {
            // Default valid token
            String token = ConfigurationManager.validateGoRestTokenPresent();
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return builder.build();
    }

    public Response createUser(UserRequest payload, String tokenMode) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec(tokenMode))
                    .body(payload)
                    .when()
                    .post()
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response createUser(UserRequest payload) {
        return createUser(payload, "default");
    }

    public Response getUser(long id) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec("default"))
                    .when()
                    .get("/" + id)
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response getUser(String id) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec("default"))
                    .when()
                    .get("/" + id)
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response getUserByEmail(String email) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec("default"))
                    .queryParam("email", email)
                    .when()
                    .get()
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response listUsers(int page, int perPage) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec("default"))
                    .queryParam("page", page)
                    .queryParam("per_page", perPage)
                    .when()
                    .get()
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response updateUser(long id, Object payload, String tokenMode) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec(tokenMode))
                    .body(payload)
                    .when()
                    .put("/" + id)
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response updateUser(String id, Object payload, String tokenMode) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec(tokenMode))
                    .body(payload)
                    .when()
                    .put("/" + id)
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response updateUser(String id, Object payload) {
        return updateUser(id, payload, "default");
    }

    public Response deleteUser(long id, String tokenMode) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec(tokenMode))
                    .when()
                    .delete("/" + id)
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response deleteUser(String id, String tokenMode) {
        return executeWithRetry(() ->
            SerenityRest.given()
                    .spec(buildBaseSpec(tokenMode))
                    .when()
                    .delete("/" + id)
                    .then()
                    .log().ifError()
                    .extract()
                    .response()
        );
    }

    public Response deleteUser(String id) {
        return deleteUser(id, "default");
    }

    /**
     * Safely deletes a test user record during teardown without throwing uncaught exceptions.
     */
    public void safeDeleteUser(long id) {
        try {
            Response response = deleteUser(id, "default");
            if (response.getStatusCode() == 204 || response.getStatusCode() == 404) {
                // Successfully cleaned up or already deleted
            }
        } catch (Exception e) {
            System.err.println("Teardown warning: Failed to clean up GoREST user ID " + id + ": " + e.getMessage());
        }
    }

    /**
     * Executes an API call with intelligent rate-limit (HTTP 429) backoff handling for live GoREST runs.
     * Inspects the Retry-After header if present, otherwise applies exponential backoff up to MAX_RETRIES.
     */
    private Response executeWithRetry(ApiAction action) {
        Response response = action.execute();
        int attempts = 0;

        while (response.getStatusCode() == 429 && attempts < MAX_RETRIES) {
            attempts++;
            long waitTimeMs = determineWaitTimeMs(response, attempts);
            System.out.printf("[GoRestClient] HTTP 429 Rate limit detected. Retry attempt %d of %d after %d ms...%n",
                    attempts, MAX_RETRIES, waitTimeMs);
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted during rate-limit backoff", e);
            }
            response = action.execute();
        }

        if (response.getStatusCode() == 429) {
            System.err.printf("[GoRestClient WARNING] Rate limit persists after %d retry attempts. Live service throttled.%n", MAX_RETRIES);
        }

        return response;
    }

    private long determineWaitTimeMs(Response response, int attempt) {
        String retryAfterHeader = response.getHeader("Retry-After");
        if (retryAfterHeader != null) {
            try {
                long seconds = Long.parseLong(retryAfterHeader.trim());
                if (seconds > 0) {
                    return seconds * 1000L;
                }
            } catch (NumberFormatException ignored) {}
        }
        // Exponential backoff: attempt 1 -> 1500ms, attempt 2 -> 3000ms, attempt 3 -> 6000ms
        return DEFAULT_BACKOFF_MS * (1L << (attempt - 1));
    }

    @FunctionalInterface
    private interface ApiAction {
        Response execute();
    }
}
