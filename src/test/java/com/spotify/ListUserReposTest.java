package com.spotify;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ListUserReposTest {

    private final String BASE_URI = "https://api.github.com";
    private final String TOKEN = System.getenv("GITHUB_PAT");
    private final String OWNER = "bpechersky"; // Replace if testing different user

    @Test
    public void listUserReposAndValidate() {
        Response response = RestAssured.given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .contentType(ContentType.JSON)
                .queryParam("per_page", 10)
                .queryParam("page", 1)
                .queryParam("affiliation", "owner")  // ← ensures only owned repos
                .when()
                .get("/user/repos")
                .then()
                .statusCode(200)
                .extract().response();


        List<String> repoOwners = response.jsonPath().getList("owner.login");
        List<Boolean> visibilities = response.jsonPath().getList("private");

        Assert.assertFalse(repoOwners.isEmpty(), "Repo list should not be empty");

        // ✅ Validate all repos belong to the authenticated user
        for (String owner : repoOwners) {
            Assert.assertEquals(owner, OWNER, "Repository owner mismatch");
        }

        // ✅ Print visibility info for reference
        System.out.println("🔍 Repositories fetched: " + repoOwners.size());
        System.out.println("🔐 Private flags: " + visibilities);
    }
}
