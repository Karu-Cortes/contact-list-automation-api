package co.com.bdb.automation.definitions.ejemplo;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class CommonDefinitions {

    private final BaseTest baseTest;

    public CommonDefinitions(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Then("I should see the response with status {int}")
    public void iShouldSeeTheResponseWithStatus(int statusCodeExpected) {
        baseTest.response.then().log().all();
        baseTest.response.then().statusCode(statusCodeExpected);
    }

    @And("the response body should be match with the schema {string}")
    public void theResponseBodyShouldBeMatchWithTheSchema(String pathSchema) {
        baseTest.response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/" + pathSchema));
    }

    @And("the property {string} must be equal to {string}")
    public void thePropertyMustBeEqualTo(String nameProperty, String valueExpected) {
        baseTest.response.then().body(nameProperty, equalTo(valueExpected));
    }

    @And("the property {string} has the item {int}")
    public void thePropertyHasTheItem(String propertyName, int item) {
        baseTest.response.then().body(propertyName, hasItem(item));
    }

    @And("the property {string} has the item {string}")
    public void thePropertyHasTheItem(String propertyName, String item) {
        baseTest.response.then().body(propertyName, hasItem(item));
    }
}
