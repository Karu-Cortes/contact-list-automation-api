package co.com.bdb.automation.definitions.ejemplo;

import co.com.bdb.automation.utilities.CustomRequestSpecification;
import co.com.bdb.automation.utilities.EnvironmentValuesTask;
import io.restassured.RestAssured;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.io.File;

public class ApiTestDefinitions {

    static EnvironmentValuesTask env = new EnvironmentValuesTask();
    private static final String BASE_URL = env.getenv("BASE_URL_APITEST");
    private CustomRequestSpecification request;
    private final BaseTest baseTest;
    private final String bodiesPath = "src/test/resources/bodies/apiTest/";
    private File bodyFile;

    public ApiTestDefinitions(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Given("I want create a post with request body {string}")
    public void iWantCreateAPostWithTitleAndBodyForUserWithID(String jsonPath) {
        bodyFile = new File(bodiesPath + jsonPath);
        request = new CustomRequestSpecification(RestAssured.given().log().all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(bodyFile)
                .basePath("/posts"));
    }

    @When("I send request to create the post")
    public void iSendRequestToCreateThePost() {
        baseTest.response = request.when().post();
    }

    @Given("I want update the post with id {int} and request body {string}")
    public void iWantUpdateTheTitleOfPostWithIdTo(int id, String jsonPath) {
        bodyFile = new File(bodiesPath + jsonPath);
        request = new CustomRequestSpecification(RestAssured.given().log().all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(bodyFile)
                .basePath("/posts/" + id));
    }

    @When("I send request to update the post")
    public void iSendRequestToUpdateThePost() {
        baseTest.response = request.when().patch();
    }

    @Given("I want delete a post with id {int}")
    public void iWantDeleteAPostWithId(int id) {
        request = new CustomRequestSpecification(RestAssured.given().log().all()
                .baseUri(BASE_URL)
                .basePath("/posts/" + id));
    }

    @When("I send request to delete the post")
    public void iSendRequestToDeleteThePostWithId() {
        baseTest.response = request.when().delete();
    }

    @Given("I want read a post of user with ID {int}")
    public void iWantReadAPostOfUserWithId(int id) {
        request = new CustomRequestSpecification(RestAssured.given().log().all()
                .baseUri(BASE_URL)
                .param("userId", id)
                .basePath("/posts"));
    }

    @When("I send request to read the post")
    public void iSendRequestToReadThePost() {
        baseTest.response = request.when().get();
    }
}
