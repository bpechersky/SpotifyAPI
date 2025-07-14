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
                .body("country", equalTo("US"))
                .body("display_name", equalTo("Boris Pechersky"))
                .body("email", equalTo("bpechersky@gmx.com"))
                .body("explicit_content.filter_enabled", equalTo(false))
                .body("explicit_content.filter_locked", equalTo(false))
                .body("external_urls.spotify", equalTo("https://open.spotify.com/user/31llaaagrkuctjrzldhi4754vujy"))
                .body("followers.total", equalTo(0))
                .body("href", equalTo("https://api.spotify.com/v1/users/31llaaagrkuctjrzldhi4754vujy"))
                .extract().response();

        System.out.println("User Profile Response: " + response.asPrettyString());
    }

    @Test
    public void getPublicUserProfile() {

        //String token = SpotifyTokenUtil.fetchAccessToken();

        RestAssured.baseURI = "https://api.spotify.com";

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/users/31llaaagrkuctjrzldhi4754vujy")
                .then()
                .statusCode(200)
                .body("display_name", equalTo("Boris Pechersky"))
                .body("external_urls.spotify", equalTo("https://open.spotify.com/user/31llaaagrkuctjrzldhi4754vujy"))
                .body("followers.href", nullValue())
                .body("followers.total", equalTo(0))
                .body("href", equalTo("https://api.spotify.com/v1/users/31llaaagrkuctjrzldhi4754vujy"))
                .body("id", equalTo("31llaaagrkuctjrzldhi4754vujy"))
                .body("images.size()", equalTo(0))
                .body("type", equalTo("user"))
                .body("uri", equalTo("spotify:user:31llaaagrkuctjrzldhi4754vujy"))
                .extract().response();

        System.out.println("Public User Profile: " + response.asPrettyString());
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



        @Test
        public void saveTrackToLibrary() {
            RestAssured.baseURI = "https://api.spotify.com";

            String requestBody = """
        {
          "ids": [
            "4iV5W9uYEdYUVa79Axb7Rh"
          ]
        }
        """;

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .put("/v1/me/tracks")
                    .then()
                    .statusCode(200); // 200 OK means track successfully saved
        }

    @Test
    public void getAlbumById() {
        RestAssured.baseURI = "https://api.spotify.com";

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .when()
                .get("/v1/albums/7MNrrItJpom6uMJWdT0XD8")
                .then()
                .statusCode(200)
                .body("album_type", equalTo("album"))
                .body("id", equalTo("7MNrrItJpom6uMJWdT0XD8"))
                .body("type", equalTo("album"))
                .body("uri", containsString("spotify:album:7MNrrItJpom6uMJWdT0XD8"))
                .body("artists[0].name", not(emptyOrNullString()))
                .body("name", not(emptyOrNullString()))
                .body("release_date", matchesRegex("\\d{4}-\\d{2}-\\d{2}"))
                .body("total_tracks", greaterThan(0))
                .extract().response();

        System.out.println("Album Info: " + response.asPrettyString());
    }
    @Test
    public void createPlaylistForUser() {
        RestAssured.baseURI = "https://api.spotify.com";



        String requestBody = """
        {
          "name": "My Cool Playlist",
          "description": "Created via API",
          "public": false
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v1/users/31llaaagrkuctjrzldhi4754vujy/playlists")
                .then()
                .statusCode(201)
                .body("name", equalTo("My Cool Playlist"))
                .body("description", equalTo("Created via API"))
                .body("public", equalTo(false))
                .body("id", notNullValue())
                .body("owner.id", equalTo("31llaaagrkuctjrzldhi4754vujy"))
                .body("type", equalTo("playlist"))
                .body("uri", containsString("spotify:playlist:"))
                .extract().response();

        System.out.println("New Playlist Response: " + response.asPrettyString());
    }

    @Test
    public void getPlaylistAndValidateFields() {


        RestAssured.baseURI = "https://api.spotify.com";

        given()
                .basePath("/v1/playlists/3cNg3v1sPgMvzDFgt0AW2H")
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("owner.display_name", notNullValue())
                .body("tracks.total", greaterThanOrEqualTo(0))
                .body("public", anyOf(is(true), is(false))); // nullable, may also be null
    }

    @Test
    public void deleteTrackFromPlaylistAndValidateResponse() {
     //   String accessToken = "BQD9fH3DdRYyLSpToBGxCq..."; // Replace with fresh token
        String playlistId = "3cNg3v1sPgMvzDFgt0AW2H";
        String trackUri = "spotify:track:4iV5W9uYEdYUVa79Axb7Rh";

        RestAssured.baseURI = "https://api.spotify.com";

        given()
                .basePath("/v1/playlists/" + playlistId + "/tracks")
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{ \"tracks\": [ { \"uri\": \"" + trackUri + "\" } ] }")
                .when()
                .delete()
                .then()
                .statusCode(200)
                .body("snapshot_id", notNullValue());
    }
    @Test
    public void validateUserLikedTracks() {

        RestAssured.baseURI = "https://api.spotify.com";

        given()
                .basePath("/v1/me/tracks")
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("items", notNullValue())
                .body("items.size()", greaterThan(0))
                .body("items[0].track.name", notNullValue())
                .body("items[0].track.artists[0].name", notNullValue())
                .body("items[0].track.album.name", notNullValue())
                .body("items[0].added_at", notNullValue());
    }

}
