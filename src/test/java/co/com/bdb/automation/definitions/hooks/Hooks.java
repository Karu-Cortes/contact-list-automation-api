package co.com.bdb.automation.definitions.hooks;

import co.com.bdb.automation.definitions.BaseTest;
import co.com.bdb.automation.utilities.ContactListApi;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class Hooks {

    private final BaseTest baseTest;

    public Hooks(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Before("@users or @auth")
    public void prepareScenario(Scenario scenario) {
        baseTest.setScenario(scenario);
    }

    @After("@users or @auth")
    public void deleteCreatedUser() {
        if (baseTest.getUserId() == null) {
            return;
        }

        if (baseTest.getScenario().getSourceTagNames().contains("@auth")) {
            Response loginResponse = ContactListApi.login(baseTest.getEmail(), baseTest.getPassword());
            loginResponse.then().log().ifValidationFails().statusCode(200)
                    .body("user._id", equalTo(baseTest.getUserId()))
                    .body("token", not(emptyOrNullString()));
            baseTest.setToken(loginResponse.path("token"));
        }

        if (baseTest.getToken() == null || baseTest.getToken().isBlank()) {
            return;
        }

        ContactListApi.authenticatedRequest(baseTest.getToken())
                .delete(ContactListApi.PROFILE_PATH)
                .then().log().ifValidationFails().statusCode(200);
        baseTest.getScenario().log("Limpieza completada: usuario %s eliminado."
                .formatted(baseTest.getUserId()));
        baseTest.setToken(null);
        baseTest.setPassword(null);
    }
}
