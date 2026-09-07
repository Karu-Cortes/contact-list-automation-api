package contactList.steps;


import contactList.utils.Config;
import contactList.utils.Logs;
import contactList.utils.RequestFilter;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class World {
    RequestSpecification request;
    Response response;

    @Before
    public void setup() {
        request = RestAssured
                .given()
                .spec(buildRequestSpecification());
    }

    @After
    public void afterScenario(Scenario scenario) {
        Logs.info(
                "afterScenario: %s, status: %s",
                scenario.getName(),
                scenario.getStatus()
        );
    }

    public static RequestSpecification buildRequestSpecification() {
        return new RequestSpecBuilder()
                .addFilter(new RequestFilter())
                .setBaseUri(Config.get("base.url"))
                .setContentType(ContentType.JSON)
                .build();
    }

    @Given("Se usa el base path {string}")
    public void asignarBasePath(String url) {
        request.basePath(url);
    }

    @When("Se envia el request con el metodo {metodoHttp}")
    public void enviarRequest(Method metodo) {
        response = request.request(metodo);
    }

    @Then("Se verifica que el status code sea {int}")
    public void verificarStatusCode(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @ParameterType("GET|POST|PUT|PATCH|DELETE")
    public Method metodoHttp(String metodo) {
        return Method.valueOf(metodo.toUpperCase());
    }
}