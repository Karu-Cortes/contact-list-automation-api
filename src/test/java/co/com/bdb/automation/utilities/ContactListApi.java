package co.com.bdb.automation.utilities;

import groovy.json.JsonOutput;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/** Configuración HTTP compartida por los steps y el hooks. */
public final class ContactListApi {

    public static final String USERS_PATH = "/users";
    public static final String PROFILE_PATH = "/users/me";
    private static final String BASE_URL = new EnvironmentValuesTask().getContactListBaseUrl();

    private ContactListApi() {
    }

    public static RequestSpecification request() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .contentType("application/json");
    }

    public static RequestSpecification authenticatedRequest(String token) {
        return request().header("Authorization", "Bearer %s".formatted(token));
    }

    public static Response login(String email, String password) {
        String body = JsonOutput.toJson(Map.of("email", email, "password", password));
        return request().body(body).post("/users/login");
    }
}
