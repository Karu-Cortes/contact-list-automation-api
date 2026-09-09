package co.com.bdb.automation.definitions;

import co.com.bdb.automation.utilities.CustomRequestSpecification;
import co.com.bdb.automation.utilities.EnvironmentValuesTask;
import groovy.json.JsonOutput;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserDefinitions {

    private static final String BASE_URL = new EnvironmentValuesTask().getContactListBaseUrl();
    private static final String CREATE_USER_BODY_PATH = "src/test/resources/bodies/contactList/createUser.json";

    private final BaseTest baseTest;
    private CustomRequestSpecification request;
    private String expectedFirstName = "Prueba";
    private String expectedLastName = "Automation";

    public UserDefinitions(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Given("que tengo un body válido para crear usuario")
    public void queTengoUnBodyValidoParaCrearUsuario() throws IOException {
        baseTest.setEmail("automation+" + UUID.randomUUID() + "@mail.com");

        String body = Files.readString(Path.of(CREATE_USER_BODY_PATH))
                .replace("{{email}}", baseTest.getEmail());

        prepareAddUserRequest(body);
    }

    @Given("que tengo un body para crear usuario con el campo {string} vacío")
    public void queTengoUnBodyParaCrearUsuarioConElCampoVacio(String field) {
        baseTest.setEmail("automation+" + UUID.randomUUID() + "@mail.com");

        String body = buildUserBodyWithEmptyField(baseTest.getEmail(), field);
        prepareAddUserRequest(body);
    }

    @Given("que ya existe un usuario registrado para crear usuario")
    public void queYaExisteUnUsuarioRegistradoParaCrearUsuario() {
        baseTest.setEmail("automation+" + UUID.randomUUID() + "@mail.com");

        String body = buildUserBody(baseTest.getEmail(), null, null);
        baseTest.setResponse(RestAssured.given().log().all()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/users"));

        baseTest.getResponse().then().statusCode(201);
        baseTest.setToken(baseTest.getResponse().path("token"));
        baseTest.setUserId(baseTest.getResponse().path("user._id"));
        prepareAddUserRequest(body);
    }

    @Given("que tengo un body para crear usuario con email inválido {string}")
    public void queTengoUnBodyParaCrearUsuarioConEmailInvalido(String email) throws IOException {
        Map<String, Object> body = newValidUserBody();
        body.put("email", email);
        prepareAddUserRequest(JsonOutput.toJson(body));
    }

    @Given("que tengo un body para crear usuario con contraseña {string}")
    public void queTengoUnBodyParaCrearUsuarioConContrasena(String password) throws IOException {
        Map<String, Object> body = newValidUserBody();
        body.put("password", password);
        prepareAddUserRequest(JsonOutput.toJson(body));
    }

    @Given("que tengo un body para crear usuario con nombre {string} y apellido {string}")
    public void queTengoUnBodyParaCrearUsuarioConNombreYApellido(String firstName, String lastName)
            throws IOException {
        Map<String, Object> body = newValidUserBody();
        expectedFirstName = firstName;
        expectedLastName = lastName;
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        prepareAddUserRequest(JsonOutput.toJson(body));
    }

    private Map<String, Object> newValidUserBody() throws IOException {
        baseTest.setEmail("automation+" + UUID.randomUUID() + "@mail.com");
        return JsonPath.from(Files.readString(Path.of(CREATE_USER_BODY_PATH))
                .replace("{{email}}", baseTest.getEmail())).getMap("$");
    }

    @When("envío la solicitud para crear el usuario con el mismo email")
    public void envioLaSolicitudParaCrearElUsuarioConElMismoEmail() {
        baseTest.setResponse(request.when().post());
    }

    @And("la respuesta de error debe contener el mensaje {string}")
    public void laRespuestaDeErrorDebeContenerElMensaje(String expectedMessage) {
        baseTest.getResponse().then().body("message", containsString(expectedMessage));
    }

    private void prepareAddUserRequest(String body) {
        request = new CustomRequestSpecification(RestAssured.given().log().all()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .basePath("/users"));
    }

    private String buildUserBody(String email, String fieldToRemove, String emailOverride) {
        List<String> fields = new ArrayList<>();

        if (!"firstName".equals(fieldToRemove)) {
            fields.add("\"firstName\": \"Prueba\"");
        }
        if (!"lastName".equals(fieldToRemove)) {
            fields.add("\"lastName\": \"Automation\"");
        }
        if (!"email".equals(fieldToRemove)) {
            fields.add("\"email\": \"" + (emailOverride != null ? emailOverride : email) + "\"");
        }
        if (!"password".equals(fieldToRemove)) {
            fields.add("\"password\": \"prueba123456\"");
        }

        return "{\n  " + String.join(",\n  ", fields) + "\n}";
    }

    private String buildUserBodyWithEmptyField(String email, String emptyField) {
        String firstName = "firstName".equals(emptyField) ? "" : "Prueba";
        String lastName = "lastName".equals(emptyField) ? "" : "Automation";
        String userEmail = "email".equals(emptyField) ? "" : email;
        String password = "password".equals(emptyField) ? "" : "prueba123456";

        return """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(firstName, lastName, userEmail, password);
    }

    @When("envío la solicitud para crear el usuario")
    public void envioLaSolicitudParaCrearElUsuario() {
        baseTest.setResponse(request.when().post());
        // Guardar antes de las aserciones permite limpiar incluso un registro inesperado.
        if (baseTest.getResponse().statusCode() == 201) {
            guardoElTokenDeAutenticacionDeContactList();
        }
    }

    @Then("la respuesta de crear usuario debe tener el status {int}")
    public void laRespuestaDeCrearUsuarioDebeTenerElStatus(int statusCodeExpected) {
        baseTest.getResponse().then().log().all();
        baseTest.getResponse().then().statusCode(statusCodeExpected);
    }

    @And("la respuesta de crear usuario debe cumplir con el schema de usuario")
    public void laRespuestaDeCrearUsuarioDebeCumplirConElSchemaDeUsuario() {
        baseTest.getResponse().then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/contactList/user.json"));
    }

    @And("la respuesta de crear usuario debe incluir los datos del usuario creado")
    public void laRespuestaDeCrearUsuarioDebeIncluirLosDatosDelUsuarioCreado() {
        baseTest.getResponse().then()
                .body("user._id", notNullValue())
                .body("user.firstName", equalTo(expectedFirstName))
                .body("user.lastName", equalTo(expectedLastName))
                .body("user.email", equalTo(baseTest.getEmail()))
                .body("token", notNullValue());
    }

    @And("guardo el token de autenticación de Contact List")
    public void guardoElTokenDeAutenticacionDeContactList() {
        baseTest.setToken(baseTest.getResponse().path("token"));
        baseTest.setUserId(baseTest.getResponse().path("user._id"));
    }
}
