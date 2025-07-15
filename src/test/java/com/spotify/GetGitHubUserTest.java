package com.spotify;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetGitHubUserTest {

    private final String BASE_URI = "https://api.github.com";
    private final String TOKEN = System.getenv("GITHUB_PAT"); // ✅ Best practice
    private final String OWNER = "bpechersky"; // Replace with your GitHub username
    private final String REPO_NAME = "test-repo-1721070000"; // The repo created in previous test


    @Test
    public void getAuthenticatedUser_shouldReturnValidProfile() {
        Response response = given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .when()
                .get("/user")
                .then()
                .statusCode(200)
                .extract().response();

        // ✅ Validate basic fields
        String login = response.jsonPath().getString("login");
        String id = response.jsonPath().getString("id");

        System.out.println("✅ GitHub login: " + login);
        System.out.println("✅ GitHub ID: " + id);

        Assert.assertNotNull(login, "Login (username) should not be null");
        Assert.assertTrue(id != null && !id.isEmpty(), "User ID should not be empty");
    }
}

