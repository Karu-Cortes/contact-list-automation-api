package co.com.bdb.automation.definitions.hooks;

import co.com.bdb.automation.definitions.BaseTest;
import co.com.bdb.automation.utilities.EnvironmentValuesTask;
import io.cucumber.java.After;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

public class Hooks {

    private static final String BASE_URL = new EnvironmentValuesTask().getenv("BASE_URL_CONTACT_LIST");

    private final BaseTest baseTest;

    public Hooks(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @After("@users")
    public void deleteCreatedUser() {
        if (baseTest.getToken() == null || baseTest.getToken().isBlank()) {
            return;
        }

        RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + baseTest.getToken())
                .when()
                .delete("/users/me");
    }
}
