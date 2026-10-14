package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.api.client.GoRestClient;
import com.atait.assignment.api.models.ApiErrorResponse;
import com.atait.assignment.api.models.UserRequest;
import com.atait.assignment.api.models.UserResponse;
import com.atait.assignment.api.models.ValidationErrorItem;
import com.atait.assignment.api.utils.GoRestTestDataGenerator;
import com.atait.assignment.context.ScenarioContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GoRestApiStepDefinitions {

    private static final String CONTEXT_CURRENT_USER = "CURRENT_USER";
    private static final String CONTEXT_FIRST_USER = "FIRST_USER";
    private static final String CONTEXT_SECOND_USER = "SECOND_USER";
    private static final String CONTEXT_TRACKED_IDS = "TRACKED_USER_IDS";
    private static final String CONTEXT_TOKEN_OVERRIDE = "TOKEN_OVERRIDE";
    private static final String CONTEXT_LATEST_REQUEST = "LATEST_REQUEST";

    private final GoRestClient client = GoRestClient.getInstance();
    private Response latestResponse;

    @Given("the GoREST API client is configured")
    public void configureGoRestClient() {
        // Initializes context tracking for the current scenario
        if (!ScenarioContext.contains(CONTEXT_TRACKED_IDS)) {
            ScenarioContext.set(CONTEXT_TRACKED_IDS, new ArrayList<Long>());
        }
        ScenarioContext.set(CONTEXT_TOKEN_OVERRIDE, "default");
    }

    @Given("the API authentication token is {string}")
    public void setApiAuthenticationToken(String tokenState) {
        ScenarioContext.set(CONTEXT_TOKEN_OVERRIDE, tokenState);
    }

    @Given("an existing user is created with payload:")
    public void createExistingUserWithPayload(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UserRequest request = buildUserRequestFromMap(data);
        latestResponse = client.createUser(request);
        latestResponse.then().statusCode(201);

        UserResponse user = latestResponse.as(UserResponse.class);
        ScenarioContext.set(CONTEXT_CURRENT_USER, user);
        trackCreatedUserId(user.getId());
    }

    @Given("two existing users are created")
    public void createTwoExistingUsers() {
        UserRequest req1 = UserRequest.builder()
                .name(GoRestTestDataGenerator.generateUniqueName("User One"))
                .email(GoRestTestDataGenerator.generateUniqueEmail("userone"))
                .gender("male")
                .status("active")
                .build();
        Response res1 = client.createUser(req1);
        res1.then().statusCode(201);
        UserResponse user1 = res1.as(UserResponse.class);
        ScenarioContext.set(CONTEXT_FIRST_USER, user1);
        trackCreatedUserId(user1.getId());

        UserRequest req2 = UserRequest.builder()
                .name(GoRestTestDataGenerator.generateUniqueName("User Two"))
                .email(GoRestTestDataGenerator.generateUniqueEmail("usertwo"))
                .gender("female")
                .status("active")
                .build();
        Response res2 = client.createUser(req2);
        res2.then().statusCode(201);
        UserResponse user2 = res2.as(UserResponse.class);
        ScenarioContext.set(CONTEXT_SECOND_USER, user2);
        trackCreatedUserId(user2.getId());
    }

    @When("I create a user with payload:")
    public void createUserWithPayload(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UserRequest request = buildUserRequestFromMap(data);
        String tokenMode = ScenarioContext.get(CONTEXT_TOKEN_OVERRIDE);
        latestResponse = client.createUser(request, tokenMode != null ? tokenMode : "default");

        if (latestResponse.getStatusCode() == 201) {
            UserResponse createdUser = latestResponse.as(UserResponse.class);
            ScenarioContext.set(CONTEXT_CURRENT_USER, createdUser);
            trackCreatedUserId(createdUser.getId());
        }
    }

    @When("I create a user with missing field {string}")
    public void createUserWithMissingField(String missingField) {
        UserRequest.Builder builder = UserRequest.builder();
        if (!"name".equalsIgnoreCase(missingField)) {
            builder.name(GoRestTestDataGenerator.generateUniqueName("Valid Name"));
        }
        if (!"email".equalsIgnoreCase(missingField)) {
            builder.email(GoRestTestDataGenerator.generateUniqueEmail("valid"));
        }
        if (!"gender".equalsIgnoreCase(missingField)) {
            builder.gender("male");
        }
        if (!"status".equalsIgnoreCase(missingField)) {
            builder.status("active");
        }

        latestResponse = client.createUser(builder.build());
    }

    @When("I create a user with invalid field {string} set to {string}")
    public void createUserWithInvalidField(String field, String invalidValue) {
        UserRequest.Builder builder = UserRequest.builder()
                .name(GoRestTestDataGenerator.generateUniqueName("Test Candidate"))
                .email(GoRestTestDataGenerator.generateUniqueEmail("valid"))
                .gender("male")
                .status("active");

        switch (field.toLowerCase()) {
            case "name" -> builder.name(invalidValue);
            case "email" -> builder.email(invalidValue);
            case "gender" -> builder.gender(invalidValue);
            case "status" -> builder.status(invalidValue);
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        }

        latestResponse = client.createUser(builder.build());
    }

    @When("I create a user with duplicate email of the existing user")
    public void createUserWithDuplicateEmail() {
        UserResponse existingUser = ScenarioContext.get(CONTEXT_CURRENT_USER);
        assertThat("Existing user must be present in context", existingUser, notNullValue());

        UserRequest duplicateRequest = UserRequest.builder()
                .name(GoRestTestDataGenerator.generateUniqueName("Duplicate User"))
                .email(existingUser.getEmail())
                .gender("female")
                .status("active")
                .build();

        latestResponse = client.createUser(duplicateRequest);
    }

    @When("I retrieve a user by ID {string}")
    public void retrieveUserById(String id) {
        String resolvedId = resolveUserId(id);
        latestResponse = client.getUser(resolvedId);
    }

    @When("I retrieve the existing user by ID")
    public void retrieveExistingUserById() {
        retrieveUserById("{user_id}");
    }

    @When("I retrieve users on page {int} with page size {int}")
    public void retrieveUsersWithPagination(int pageNumber, int pageSize) {
        latestResponse = client.listUsers(pageNumber, pageSize);
    }

    @When("I update a user by ID {string} with payload:")
    public void updateUserByIdWithPayload(String id, DataTable dataTable) {
        String resolvedId = resolveUserId(id);
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String tokenMode = ScenarioContext.get(CONTEXT_TOKEN_OVERRIDE);
        latestResponse = client.updateUser(resolvedId, data, tokenMode != null ? tokenMode : "default");

        if (latestResponse.getStatusCode() == 200) {
            UserResponse updated = latestResponse.as(UserResponse.class);
            ScenarioContext.set(CONTEXT_CURRENT_USER, updated);
        }
    }

    @When("I update the existing user with payload:")
    public void updateExistingUserWithPayload(DataTable dataTable) {
        updateUserByIdWithPayload("{user_id}", dataTable);
    }

    @When("I update a user by ID {string} with the email of user {string}")
    public void updateUserByIdWithUserEmail(String targetUserId, String sourceUserId) {
        String resolvedTargetId = resolveUserId(targetUserId);

        UserResponse sourceUser;
        if ("{first_user_id}".equalsIgnoreCase(sourceUserId) || "first".equalsIgnoreCase(sourceUserId)) {
            sourceUser = ScenarioContext.get(CONTEXT_FIRST_USER);
        } else if ("{second_user_id}".equalsIgnoreCase(sourceUserId) || "second".equalsIgnoreCase(sourceUserId)) {
            sourceUser = ScenarioContext.get(CONTEXT_SECOND_USER);
        } else {
            sourceUser = ScenarioContext.get(CONTEXT_CURRENT_USER);
        }
        assertThat("Source user must be present in context", sourceUser, notNullValue());

        Map<String, String> updatePayload = Map.of(
                "email", sourceUser.getEmail()
        );
        String tokenMode = ScenarioContext.get(CONTEXT_TOKEN_OVERRIDE);
        latestResponse = client.updateUser(resolvedTargetId, updatePayload, tokenMode != null ? tokenMode : "default");
    }

    @When("I update the first user with the email of the second user")
    public void updateFirstUserWithSecondUserEmail() {
        updateUserByIdWithUserEmail("{first_user_id}", "{second_user_id}");
    }

    @When("I delete a user by ID {string}")
    public void deleteUserById(String id) {
        String resolvedId = resolveUserId(id);
        String tokenMode = ScenarioContext.get(CONTEXT_TOKEN_OVERRIDE);
        latestResponse = client.deleteUser(resolvedId, tokenMode != null ? tokenMode : "default");
        if (latestResponse.getStatusCode() == 204) {
            try {
                untrackCreatedUserId(Long.parseLong(resolvedId));
            } catch (NumberFormatException ignored) {}
        }
    }

    @When("I delete the existing user")
    public void deleteExistingUser() {
        deleteUserById("{user_id}");
    }

    @When("I delete both test users")
    public void deleteBothTestUsers() {
        deleteUserById("{first_user_id}");
        deleteUserById("{second_user_id}");
    }

    // =========================================================================
    // Reusable Assertions (Typed POJOs & Robust Matchers)
    // =========================================================================

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat("Response must not be null", latestResponse, notNullValue());
        latestResponse.then().statusCode(expectedStatusCode);
    }

    @Then("the response body should contain a valid user id")
    public void verifyValidUserId() {
        latestResponse.then()
                .body("id", notNullValue())
                .body("id", isA(Number.class));
    }

    @Then("the response body should conform to the user schema")
    public void verifyResponseBodyConformsToUserSchema() {
        UserResponse user = latestResponse.as(UserResponse.class);
        assertThat("User response must not be null", user, notNullValue());
        assertThat("User ID must be positive", user.getId(), greaterThan(0L));
        assertThat("User Name must not be blank", user.getName(), not(emptyOrNullString()));
        assertThat("User Email must be valid email format", user.getEmail(), matchesPattern("^[A-Za-z0-9+_.-]+@(.+)$"));
        assertThat("User Gender must be male or female", user.getGender(), anyOf(equalTo("male"), equalTo("female")));
        assertThat("User Status must be active or inactive", user.getStatus(), anyOf(equalTo("active"), equalTo("inactive")));
    }

    @Then("the response body field {string} should be {string}")
    public void verifyBodyFieldEquals(String field, String expectedValue) {
        if ("name".equalsIgnoreCase(field)) {
            String actualName = latestResponse.jsonPath().getString("name");
            assertThat("Name field starts with or equals expected prefix",
                    actualName, startsWith(expectedValue));
        } else {
            latestResponse.then().body(field, equalTo(expectedValue));
        }
    }

    @Then("the response body should match the attributes of the existing user")
    public void verifyBodyMatchesExistingUser() {
        UserResponse expected = ScenarioContext.get(CONTEXT_CURRENT_USER);
        assertThat("Existing user must be in context", expected, notNullValue());

        UserResponse actual = latestResponse.as(UserResponse.class);
        assertThat("ID matches", actual.getId(), equalTo(expected.getId()));
        assertThat("Name matches", actual.getName(), equalTo(expected.getName()));
        assertThat("Email matches", actual.getEmail(), equalTo(expected.getEmail()));
        assertThat("Gender matches", actual.getGender(), equalTo(expected.getGender()));
        assertThat("Status matches", actual.getStatus(), equalTo(expected.getStatus()));
    }

    @Then("the response validation error for {string} should be {string}")
    public void verifyValidationError(String field, String expectedMessage) {
        ValidationErrorItem[] errors = latestResponse.as(ValidationErrorItem[].class);
        assertThat("Validation errors list must not be null", errors, notNullValue());
        assertThat("Validation errors list must not be empty", errors.length, greaterThan(0));

        boolean found = Arrays.stream(errors).anyMatch(err ->
                field.equalsIgnoreCase(err.getField()) &&
                err.getMessage() != null &&
                err.getMessage().contains(expectedMessage)
        );

        assertThat(String.format("Expected validation error for field '%s' with message '%s'. Actual errors: %s",
                field, expectedMessage, Arrays.toString(errors)), found, is(true));
    }

    @Then("the response error message should be {string}")
    public void verifyErrorMessage(String expectedMessage) {
        ApiErrorResponse error = latestResponse.as(ApiErrorResponse.class);
        assertThat("Error response must not be null", error, notNullValue());
        assertThat("Error message matches", error.getMessage(), equalTo(expectedMessage));
    }

    @Then("the response error message should be present")
    public void verifyErrorMessagePresent() {
        ApiErrorResponse error = latestResponse.as(ApiErrorResponse.class);
        assertThat("Error response must not be null", error, notNullValue());
        assertThat("Error message must not be blank", error.getMessage(), not(emptyOrNullString()));
    }

    @Then("no user record should be created")
    public void verifyNoUserRecordCreated() {
        latestResponse.then().body("id", nullValue());
        UserRequest lastReq = ScenarioContext.get(CONTEXT_LATEST_REQUEST);
        if (lastReq != null && lastReq.getEmail() != null) {
            Response queryRes = client.getUserByEmail(lastReq.getEmail());
            if (queryRes.getStatusCode() == 200) {
                List<?> items = queryRes.jsonPath().getList("");
                assertThat("Querying by attempted email must return empty list", items.isEmpty(), is(true));
            }
        }
    }

    @Then("the response body should contain {int} items")
    public void verifyItemCount(int expectedCount) {
        List<?> items = latestResponse.jsonPath().getList("");
        assertThat("Item count matches", items.size(), equalTo(expectedCount));
    }

    @Then("the response headers should contain:")
    public void verifyHeadersExist(DataTable dataTable) {
        List<String> rawHeaders = dataTable.asList(String.class);
        List<String> expectedHeaders = rawHeaders.stream()
                .filter(h -> !"header".equalsIgnoreCase(h.trim()))
                .toList();
        for (String headerName : expectedHeaders) {
            String value = latestResponse.getHeader(headerName);
            assertThat("Header '" + headerName + "' must be present in response", value, notNullValue());
        }
    }

    @Then("the response time should be less than {long} milliseconds")
    public void verifyResponseTime(long maxMillis) {
        latestResponse.then().time(lessThan(maxMillis));
    }

    // =========================================================================
    // Helper Methods
    // =========================================================================

    private String resolveUserId(String idParam) {
        if (idParam == null || idParam.isBlank()) {
            throw new IllegalArgumentException("User ID parameter cannot be null or blank");
        }
        String trimmed = idParam.trim();
        if (trimmed.equalsIgnoreCase("{id}")
                || trimmed.equalsIgnoreCase("{user_id}")
                || trimmed.equalsIgnoreCase("{created_user_id}")
                || trimmed.equalsIgnoreCase("existing")
                || trimmed.equalsIgnoreCase("current")) {
            UserResponse current = ScenarioContext.get(CONTEXT_CURRENT_USER);
            assertThat("Current user must be present in context", current, notNullValue());
            return String.valueOf(current.getId());
        }
        if (trimmed.equalsIgnoreCase("{first_user_id}")
                || trimmed.equalsIgnoreCase("first")) {
            UserResponse first = ScenarioContext.get(CONTEXT_FIRST_USER);
            assertThat("First user must be present in context", first, notNullValue());
            return String.valueOf(first.getId());
        }
        if (trimmed.equalsIgnoreCase("{second_user_id}")
                || trimmed.equalsIgnoreCase("second")) {
            UserResponse second = ScenarioContext.get(CONTEXT_SECOND_USER);
            assertThat("Second user must be present in context", second, notNullValue());
            return String.valueOf(second.getId());
        }
        return trimmed;
    }

    private UserRequest buildUserRequestFromMap(Map<String, String> data) {
        String name = data.get("name");
        String email = data.get("email");
        String gender = data.get("gender");
        String status = data.get("status");

        if (name == null || "dynamic".equalsIgnoreCase(name)) {
            name = GoRestTestDataGenerator.generateUniqueName("Test User");
        } else if (!name.contains("(QA")) {
            name = GoRestTestDataGenerator.generateUniqueName(name);
        }

        if (email == null || "dynamic".equalsIgnoreCase(email)) {
            email = GoRestTestDataGenerator.generateUniqueEmail(name);
        }

        UserRequest req = UserRequest.builder()
                .name(name)
                .email(email)
                .gender(gender)
                .status(status)
                .build();
        ScenarioContext.set(CONTEXT_LATEST_REQUEST, req);
        return req;
    }

    private void trackCreatedUserId(Long id) {
        if (id == null) return;
        List<Long> trackedIds = ScenarioContext.get(CONTEXT_TRACKED_IDS);
        if (trackedIds == null) {
            trackedIds = new ArrayList<>();
            ScenarioContext.set(CONTEXT_TRACKED_IDS, trackedIds);
        }
        if (!trackedIds.contains(id)) {
            trackedIds.add(id);
        }
    }

    private void untrackCreatedUserId(Long id) {
        if (id == null) return;
        List<Long> trackedIds = ScenarioContext.get(CONTEXT_TRACKED_IDS);
        if (trackedIds != null) {
            trackedIds.remove(id);
        }
    }
}
