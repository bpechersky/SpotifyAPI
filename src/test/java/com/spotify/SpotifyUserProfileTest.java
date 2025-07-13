package com.spotify;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SpotifyUserProfileTest {

    private static final String ACCESS_TOKEN = "BQA-1H3fmN7UmRAzyX6MUFMBy2tDimFIZjoL3VFQR0VHpcfYJvarkRYWzRDphKI_I0c0Pai7FqDt8IXi0zum8Czq0S4UvZ3Oy5cvczlam4SS-F_if8lRvUyIHMoBx32Ams_SkbIjb3u64UGKVqB5seAHuAhYVK-FH4PBs0LWbr_UzLy0pV0_oQw3LIlHrWrhCy2h6_bHD7jieyS7W3YyyXLB0zKWADpsWIhSpId-9-7uqLpEJEys_I_VXYyGbw0ues8ZXQZR71S3bNI6QW5A_qIOHIFEW0nL0Lnm8vGQ7LEKbRs3iAnmEBjYfD9WRoMGvL5JWA";

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
