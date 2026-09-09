package co.com.bdb.automation.definitions;

import co.com.bdb.automation.utilities.ContactListApi;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class LoginDefinitions {

    private static final String CREATE_USER_BODY_PATH = "src/test/resources/bodies/contactList/createUser.json";
    private final BaseTest baseTest;

    public LoginDefinitions(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Given("que tengo un usuario registrado para probar el login")
    public void crearUsuarioParaLogin() throws IOException {
        String email = "automation+%s@mail.com".formatted(UUID.randomUUID());
        String body = Files.readString(Path.of(CREATE_USER_BODY_PATH))
                .replace("{{email}}", email);
        baseTest.setPassword(JsonPath.from(body).getString("password"));
        baseTest.setEmail(email);

        Response response = ContactListApi.request().body(body).post(ContactListApi.USERS_PATH);
        if (response.statusCode() == 201) {
            baseTest.setUserId(response.path("user._id"));
            baseTest.setToken(response.path("token"));
        }
        response.then().log().ifValidationFails().statusCode(201);
    }

    @When("inicio sesión con las credenciales correctas")
    public void iniciarSesion() {
        Response response = ContactListApi.login(baseTest.getEmail(), baseTest.getPassword());
        baseTest.setResponse(response);
        if (response.statusCode() == 200) {
            baseTest.setToken(response.path("token"));
        }
    }

    @When("inicio sesión con una contraseña incorrecta")
    public void iniciarSesionConContrasenaIncorrecta() {
        String incorrectPassword = "%s-incorrecta".formatted(baseTest.getPassword());
        baseTest.setResponse(ContactListApi.login(baseTest.getEmail(), incorrectPassword));
    }

    @Then("la respuesta de autenticación debe tener el status {int}")
    public void validarStatus(int status) {
        baseTest.getResponse().then().log().ifValidationFails().statusCode(status);
    }

    @When("cierro la sesión del usuario")
    public void cerrarSesion() {
        baseTest.setResponse(ContactListApi.authenticatedRequest(baseTest.getToken()).post("/users/logout"));
    }
}
