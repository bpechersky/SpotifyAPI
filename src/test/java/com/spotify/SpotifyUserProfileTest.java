package com.spotify;
//comment
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.spotify.util.SpotifyTokenUtil;




import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SpotifyUserProfileTest {

    private static final String ACCESS_TOKEN = "BQCjEsOJS9UiTdmU9Fox9GFOUShJoROZ3KbMpB54nB6N0CPKjbZzWMrteDkkh4zOBhopezeUmwP6d88KMR2nAMb_uPX6s4WUui1eMyl-Eoc43bwGK3EsAFMdLXbqmxXgTYCSqFlOj3L0G068HeBDJdZcAlhoyddmET1OJivwdlrdepBZaqu-MgvtWnbJiQr6dsi8YhXse4CDeCDxmM6dfGrZTsq1WClAdUgyo0_srCsQU9Sn8QxZWwaMhJ9r8yWHSim3jDGP69PGYcZFjH_AWMpil7cQU2D2yHAEDBnFgg1Z6lHqsP7JCpsAhmoZoW73yV-dI_A8d_IuMcIhmjjsjgDU5itoSTtMvCwJQ77QWduw8A7dvl_uQ9Fbja4Z";
    String token = SpotifyTokenUtil.fetchAccessToken();
    @Test
    public void getCurrentUserProfile() {
        RestAssured.baseURI = "https://api.spotify.com";

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
        .when()
                .get("/v1/me")
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("type", equalTo("user"))
                .extract().response();

        System.out.println("User Profile Response: " + response.asPrettyString());
    }

    @Test
    public void getPublicUserProfile() {

        //String token = SpotifyTokenUtil.fetchAccessToken();

        RestAssured.baseURI = "https://api.spotify.com";

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/users/31llaaagrkuctjrzldhi4754vujy")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    public void getTrackById() {


        RestAssured.baseURI = "https://api.spotify.com";

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .when()
                .get("/v1/tracks/4iV5W9uYEdYUVa79Axb7Rh")
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("id", equalTo("4iV5W9uYEdYUVa79Axb7Rh"))
                .extract().response();

        System.out.println("Track Info: " + response.asPrettyString());
    }
}
