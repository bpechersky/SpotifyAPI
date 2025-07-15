package com.spotify;

import com.spotify.util.RepoContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class GitHubRepoAndIssueFlowTest {

    private final String BASE_URI = "https://api.github.com";
    private final String TOKEN = System.getenv("GITHUB_PAT");
    private final String OWNER = "bpechersky";
    private static String repoName;
    private static int issueNumber;
    private static long commentId;

    @Test(priority = 1)
    public void createRepo() {
        repoName = "test-repo-" + System.currentTimeMillis();
        String body = "{ \"name\": \"" + repoName + "\", \"private\": false, \"auto_init\": true }";

        given()
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
        RepoContext.repoName = repoName;
    }

    @Test(priority = 2, dependsOnMethods = "createRepo")
    public void starRepo() {
        Assert.assertNotNull(RepoContext.repoName, "❌ repoName was not set!");
        given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .when()
                .put("/user/starred/" + RepoContext.owner + "/" + RepoContext.repoName)
                .then()
                .statusCode(204);

        System.out.println("⭐ Starred repo: " + RepoContext.repoName);
    }

    @Test(priority = 3, dependsOnMethods = "starRepo")
    public void updateRepoDescription() {
        String body = "{ \"description\": \"Updated repo via API\" }";

        given()
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

    @Test(priority = 4, dependsOnMethods = "updateRepoDescription")
    public void createIssue() {
        String body = "{ \"title\": \"Issue Title\", \"body\": \"Issue body from test\" }";

        Response response = given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/repos/" + OWNER + "/" + repoName + "/issues");

        response.then().statusCode(201);
        issueNumber = response.jsonPath().getInt("number");

        Assert.assertTrue(issueNumber > 0);
        System.out.println("✅ Created issue #" + issueNumber);
    }

    @Test(priority = 5, dependsOnMethods = "createIssue")
    public void updateIssue() {
        String body = "{ \"title\": \"Updated Issue\", \"body\": \"Updated via API\" }";

        given()
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

    @Test(priority = 6, dependsOnMethods = "updateIssue")
    public void closeIssue() {
        String body = "{ \"state\": \"closed\" }";

        given()
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

    @Test(priority = 7, dependsOnMethods = "closeIssue")
    public void addCommentToIssue() {
        Response response = given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .body("{\"body\": \"This is a test comment from API\"}")
                .when()
                .post("/repos/" + OWNER + "/" + repoName + "/issues/" + issueNumber + "/comments");

        response.then()
                .statusCode(201)
                .log().body();

        String rawResponse = response.asString();
        System.out.println("💬 Raw comment response: " + rawResponse);

        long extractedId = response.jsonPath().getLong("id");
        System.out.println("✅ Extracted commentId = " + extractedId);

        Assert.assertTrue(extractedId > 0, "❌ Extracted commentId was invalid or missing");

        commentId = extractedId;
    }

    @Test(priority = 8, dependsOnMethods = "addCommentToIssue")
    public void getAllIssuesForRepo() {
        given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .queryParam("state", "all") // Include closed issues
                .when()
                .get("/repos/" + OWNER + "/" + repoName + "/issues")
                .then()
                .statusCode(200)
                .log().body()
                .assertThat().body("size()", greaterThanOrEqualTo(1));

        System.out.println("📄 Verified issue list contains at least 1 issue");
    }


    @Test(priority = 9, dependsOnMethods = "getAllIssuesForRepo")
    public void deleteComment() {
        given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .when()
                .delete("/repos/" + OWNER + "/" + repoName + "/issues/comments/" + commentId)
                .then()
                .statusCode(204);

        System.out.println("❌ Deleted comment ID: " + commentId);
    }

    @Test(priority = 10, dependsOnMethods = "deleteComment")
    public void deleteRepo() {
        given()
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
