package com.atait.assignment;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.qameta.allure.restassured.AllureRestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RestAssuredSampleTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @DisplayName("GET /posts/1 - Verify status code and response payload")
    void shouldGetPostById() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("userId", equalTo(1))
            .body("title", notNullValue())
            .body("body", notNullValue());
    }

    @Test
    @DisplayName("POST /posts - Verify creating a new resource")
    void shouldCreateNewPost() {
        String requestBody = """
            {
                "title": "foo",
                "body": "bar",
                "userId": 1
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body("title", equalTo("foo"))
            .body("body", equalTo("bar"))
            .body("userId", equalTo(1))
            .body("id", notNullValue());
    }
}
