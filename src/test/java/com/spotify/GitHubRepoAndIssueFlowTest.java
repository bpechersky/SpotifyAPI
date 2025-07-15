package com.spotify;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GitHubRepoAndIssueFlowTest {

    private final String BASE_URI = "https://api.github.com";
    private final String TOKEN = System.getenv("GITHUB_PAT"); // Set env var before running
    private final String OWNER = "bpechersky";
    private static String repoName;
    private static int issueNumber;

    @Test(priority = 1)
    public void createRepo() {
        repoName = "test-repo-" + System.currentTimeMillis();
        String body = "{ \"name\": \"" + repoName + "\", \"private\": false, \"auto_init\": true }";

        RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/user/repos")
                .then()
                .statusCode(201);

        System.out.println("✅ Created repo: " + repoName);
    }

    @Test(priority = 2, dependsOnMethods = "createRepo")
    public void updateRepoDescription() {
        String body = "{ \"description\": \"Updated repo via API\" }";

        RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch("/repos/" + OWNER + "/" + repoName)
                .then()
                .statusCode(200);

        System.out.println("✏️ Updated repo description");
    }

    @Test(priority = 3, dependsOnMethods = "updateRepoDescription")
    public void createIssue() {
        String body = "{ \"title\": \"Issue Title\", \"body\": \"Issue body from test\" }";

        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/repos/" + OWNER + "/" + repoName + "/issues")
                .then()
                .statusCode(201)
                .extract().response();

        issueNumber = response.jsonPath().getInt("number");
        Assert.assertTrue(issueNumber > 0);
        System.out.println("✅ Created issue #" + issueNumber);
    }

    @Test(priority = 4, dependsOnMethods = "createIssue")
    public void updateIssue() {
        String body = "{ \"title\": \"Updated Issue\", \"body\": \"Updated via API\" }";

        RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch("/repos/" + OWNER + "/" + repoName + "/issues/" + issueNumber)
                .then()
                .statusCode(200);

        System.out.println("✏️ Updated issue #" + issueNumber);
    }

    @Test(priority = 5, dependsOnMethods = "updateIssue")
    public void closeIssue() {
        String body = "{ \"state\": \"closed\" }";

        RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch("/repos/" + OWNER + "/" + repoName + "/issues/" + issueNumber)
                .then()
                .statusCode(200);

        System.out.println("🗑️ Closed issue #" + issueNumber);
    }

    @Test(priority = 6, dependsOnMethods = "closeIssue")
    public void deleteRepo() {
        RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .when()
                .delete("/repos/" + OWNER + "/" + repoName)
                .then()
                .statusCode(204);

        System.out.println("🧹 Deleted repo: " + repoName);
    }
}
