package com.spotify.util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SpotifyTokenUtil {
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_ID = "c1fd23fb17ea440190e1197e2e2b697b";
    private static final String CLIENT_SECRET = "81c830c485af4b58aa4e5fca2d408439";
    private static final String REFRESH_TOKEN = "AQAwjuRMt2bzVDTyONFgqIXyJTPu4g0hp0o4hZ8EjYXcrRFWENgaggUKfbJqMhFr4S_dOFyz6qy6xaPeQQ8G8L8zyGjemreSJs1T_Xo6H0ihbCEvPT77aAnxyG5mX8ZwyXE";

    public static String fetchAccessToken() {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", REFRESH_TOKEN);

        Response response = RestAssured
                .given()
                .contentType(ContentType.URLENC)
                .header("Authorization", "Basic " + encodedCredentials)
                .formParams(params)
                .when()
                .post(TOKEN_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Extract the token to a variable
        String accessToken = response.jsonPath().getString("access_token");

        // ✅ Print it
        System.out.println("Access Token: " + accessToken);
        System.out.println("Scopes: " + response.jsonPath().getString("scope"));


        return accessToken;
    }


}
