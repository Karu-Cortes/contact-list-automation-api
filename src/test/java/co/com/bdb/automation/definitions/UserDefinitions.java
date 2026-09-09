package co.com.bdb.automation.definitions;

import co.com.bdb.automation.utilities.EnvironmentValuesTask;
import groovy.json.JsonOutput;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserDefinitions {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDefinitions.class);
    private Scenario scenario;

    private static final String BASE_URL = new EnvironmentValuesTask().getContactListBaseUrl();
    private static final String CREATE_USER_BODY_PATH = "src/test/resources/bodies/contactList/createUser.json";
    private static final String UPDATE_USER_BODY_PATH = "src/test/resources/bodies/contactList/updateUser.json";

    private final BaseTest baseTest;
    private Map<String, Object> body;
    private String expectedFirstName = "Prueba";
    private String expectedLastName = "Automation";

    public UserDefinitions(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Before("@delete")
    public void guardarEscenarioParaElLog(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("que tengo un body válido para crear usuario")
    public void queTengoUnBodyValidoParaCrearUsuario() throws IOException {
        body = newValidUserBody();
    }

    @Given("que tengo un body para crear usuario con el campo {string} vacío")
    public void queTengoUnBodyParaCrearUsuarioConElCampoVacio(String field) throws IOException {
        body = newValidUserBody();
        if (!body.containsKey(field)) {
            throw new IllegalArgumentException("Campo de usuario desconocido: %s".formatted(field));
        }
        body.put(field, "");
    }

    @Given("que ya existe un usuario registrado para crear usuario")
    public void queYaExisteUnUsuarioRegistradoParaCrearUsuario() throws IOException {
        createRegisteredUser();
    }

    @Given("que tengo un body para crear usuario con email inválido {string}")
    public void queTengoUnBodyParaCrearUsuarioConEmailInvalido(String email) throws IOException {
        body = newValidUserBody();
        body.put("email", email);
    }

    @Given("que tengo un body para crear usuario con contraseña {string}")
    public void queTengoUnBodyParaCrearUsuarioConContrasena(String password) throws IOException {
        body = newValidUserBody();
        body.put("password", password);
    }

    @Given("que tengo un body para crear usuario con nombre {string} y apellido {string}")
    public void queTengoUnBodyParaCrearUsuarioConNombreYApellido(String firstName, String lastName)
            throws IOException {
        body = newValidUserBody();
        expectedFirstName = firstName;
        expectedLastName = lastName;
        body.put("firstName", firstName);
        body.put("lastName", lastName);
    }

    private Map<String, Object> newValidUserBody() throws IOException {
        return readUserBody(CREATE_USER_BODY_PATH);
    }

    @When("envío la solicitud para crear el usuario")
    @When("envío la solicitud para crear el usuario con el mismo email")
    public void envioLaSolicitudParaCrearElUsuario() {
        createUser();
    }

    @And("la respuesta de error debe contener el mensaje {string}")
    public void laRespuestaDeErrorDebeContenerElMensaje(String expectedMessage) {
        baseTest.getResponse().then().body("message", containsString(expectedMessage));
    }

    // Configuración de peticiones
    private RequestSpecification userRequest() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URL)
                .contentType("application/json");
    }

    private void createUser() {
        baseTest.setResponse(userRequest().body(JsonOutput.toJson(body)).post("/users"));
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

    @Given("que tengo un usuario registrado y autenticado")
    public void queTengoUnUsuarioRegistradoYAutenticado() throws IOException {
        createRegisteredUser();
    }

    private void createRegisteredUser() throws IOException {
        body = newValidUserBody();
        createUser();
        baseTest.getResponse().then().statusCode(201)
                .body("token", notNullValue())
                .body("user._id", notNullValue());
    }

    @Given("que tengo un body válido para actualizar el usuario")
    public void queTengoUnBodyValidoParaActualizarElUsuario() throws IOException {
        body = readUserBody(UPDATE_USER_BODY_PATH);
    }

    private RequestSpecification authenticatedProfileRequest() {
        return userRequest()
                .header("Authorization", "Bearer %s".formatted(baseTest.getToken()));
    }

    @When("envío la solicitud para actualizar el usuario")
    public void envioLaSolicitudParaActualizarElUsuario() {
        baseTest.setResponse(authenticatedProfileRequest()
                .body(JsonOutput.toJson(body)).patch("/users/me"));
    }

    @When("consulto el perfil del usuario autenticado")
    public void consultoElPerfilDelUsuarioAutenticado() {
        baseTest.setResponse(authenticatedProfileRequest().get("/users/me"));
    }

    @When("envío la solicitud para eliminar el usuario")
    public void envioLaSolicitudParaEliminarElUsuario() {
        baseTest.setResponse(authenticatedProfileRequest().delete("/users/me"));
    }

    @Then("la respuesta de eliminar usuario debe tener el status 200")
    public void laRespuestaDeEliminarUsuarioDebeTenerElStatus200() {
        baseTest.getResponse().then().log().ifValidationFails().statusCode(200);

        // Registrar el éxito solo después de validar la respuesta del DELETE.
        String message = "Usuario eliminado correctamente. ID: %s, email: %s. DELETE: 200."
                .formatted(baseTest.getUserId(), baseTest.getEmail());
        scenario.log(message);
        LOGGER.info(message);
        // El hook de limpieza ya no necesita eliminar este usuario.
        baseTest.setToken(null);
    }

    @Then("la respuesta del perfil de usuario debe tener el status {int}")
    public void laRespuestaDelPerfilDeUsuarioDebeTenerElStatus(int status) {
        baseTest.getResponse().then().log().ifValidationFails().statusCode(status);
    }

    @And("el perfil debe contener los datos esperados del usuario")
    public void elPerfilDebeContenerLosDatosEsperadosDelUsuario() {
        baseTest.getResponse().then()
                .body("_id", equalTo(baseTest.getUserId()))
                .body("firstName", equalTo(expectedFirstName))
                .body("lastName", equalTo(expectedLastName))
                .body("email", equalTo(baseTest.getEmail()));
    }

    //guarda los datos que se esperan de la API.
    private Map<String, Object> readUserBody(String path) throws IOException {
        String email = "automation+%s@mail.com".formatted(UUID.randomUUID());
        String json = Files.readString(Path.of(path)).replace("{{email}}", email);
        Map<String, Object> userBody = JsonPath.from(json).getMap("$");
        baseTest.setEmail(email);
        expectedFirstName = (String) userBody.get("firstName");
        expectedLastName = (String) userBody.get("lastName");
        return userBody;
    }
}
