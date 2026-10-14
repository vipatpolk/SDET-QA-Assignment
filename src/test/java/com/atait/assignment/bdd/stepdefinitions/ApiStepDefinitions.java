package com.atait.assignment.bdd.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;

import static net.serenitybdd.rest.SerenityRest.given;
import static org.hamcrest.Matchers.*;

public class ApiStepDefinitions {
    private RequestSpecification request;
    private Response response;

    @Given("the API base URI is {string}")
    public void setBaseUri(String baseUri) {
        SerenityRest.setDefaultBasePath("");
        request = given().baseUri(baseUri).contentType(ContentType.JSON);
    }

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = request.when().get(endpoint);
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

        response = request.body(payload).when().post(endpoint);
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
}
