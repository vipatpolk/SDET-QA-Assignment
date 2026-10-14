@api
Feature: Posts API Testing
  As an API consumer
  I want to interact with the Posts API
  So that I can verify data retrieval and resource creation

  @smoke
  Scenario: Successfully retrieve a post by ID
    Given the API base URI is "https://jsonplaceholder.typicode.com"
    When I send a GET request to "/posts/1"
    Then the response status code should be 200
    And the response body should contain "id" with value 1
    And the response body should contain "userId" with value 1

  @regression
  Scenario: Successfully create a new post
    Given the API base URI is "https://jsonplaceholder.typicode.com"
    When I send a POST request to "/posts" with title "BDD Test Title", body "BDD Test Body", and userId 1
    Then the response status code should be 201
    And the response body should contain "title" with value "BDD Test Title"
    And the response body should contain a valid id
