package com.atait.assignment.bdd.stepdefinitions;

import com.atait.assignment.config.ConfigurationManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;

import static org.hamcrest.Matchers.*;

/**
 * Exemplary Step Definition demonstrating API testing best practices:
 * 1. ConfigurationManager integration (zero hardcoding)
 * 2. SerenityRest for living documentation reporting
 * 3. AAA pattern (Arrange in Given, Act in When, Assert in Then)
 * 4. Latency SLA assertions (< N milliseconds)
 * 5. Reusable matchers for status codes and payloads
 */
public class RestAssuredStepExamples {

    private RequestSpecification requestSpec;
    private Response response;

    @Given("the API endpoint is configured")
    public void configureApiEndpoint() {
        String baseUri = ConfigurationManager.getProperty("api.base.url", "https://jsonplaceholder.typicode.com");
        
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = SerenityRest.given()
                .spec(requestSpec)
                .when()
                .get(endpoint);
    }

    @When("I send a POST request to {string} with title {string}, body {string}, and userId {int}")
    public void sendPostRequest(String endpoint, String title, String body, int userId) {
        String payload = String.format("""
            {
                "title": "%s",
                "body": "%s",
                "userId": %d
            }
            """, title, body, userId);

        response = SerenityRest.given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post(endpoint);
    }

    @When("I send a PUT request to {string} with title {string}, body {string}, and userId {int}")
    public void sendPutRequest(String endpoint, String title, String body, int userId) {
        String payload = String.format("""
            {
                "id": 1,
                "title": "%s",
                "body": "%s",
                "userId": %d
            }
            """, title, body, userId);

        response = SerenityRest.given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .put(endpoint);
    }

    @When("I send a DELETE request to {string}")
    public void sendDeleteRequest(String endpoint) {
        response = SerenityRest.given()
                .spec(requestSpec)
                .when()
                .delete(endpoint);
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Then("the response body should contain {string} with value {int}")
    public void verifyBodyIntField(String field, int expectedValue) {
        response.then().body(field, equalTo(expectedValue));
    }

    @Then("the response body should contain {string} with value {string}")
    public void verifyBodyStringField(String field, String expectedValue) {
        response.then().body(field, equalTo(expectedValue));
    }

    @Then("the response body should contain a valid id")
    public void verifyValidId() {
        response.then().body("id", notNullValue());
    }

    @Then("the response time should be less than {long} milliseconds")
    public void verifyResponseTime(long maxMillis) {
        response.then().time(lessThan(maxMillis));
    }
}
