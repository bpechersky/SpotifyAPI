package com.spotify;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SpotifyUserProfileTest {

    private static final String ACCESS_TOKEN = "BQDqkqK11PsxPx4AhsUMh6G-RsnVyKNFZhzhY9Mirv1nUHyR92JnFsInH7MIrdXOJuUod8XQh5zXp2uGvoKQN5RS-dps5mOv-_02p1T3Qqrc5J4cEMON6318BeM3cprg0KoCqRX-50xR4jP_hWPV6YU-sbMy_Xwwx0yU6vpG9OFLCcZENXs8ngvLU_gJ6t1HVQyBolHSW3rs_WzeyrUY27Ww2qNb5hUxGfnHoUEuedbvoU8eRXJcvIZ288qrpl4Ii9XsLWGjtGJVE3B5RuUY0uD8QXllLPvFnimtOfYnCPpMmdBCUyftAk4rmbd4l4IGBoHfSfVUPAYYWOA5sosqE7bHXizK-P25QQieDV7LvsfkqaWNDXobTqnoAbdU";

    @Test
    public void getCurrentUserProfile() {
        RestAssured.baseURI = "https://api.spotify.com";

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
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
}
