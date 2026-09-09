package co.com.bdb.automation.definitions;

import co.com.bdb.automation.utilities.ContactListApi;
import groovy.json.JsonOutput;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

public class ContactDefinitions {

    private final BaseTest baseTest;
    private Map<String, Object> body;
    private Map<String, Object> expectedContact;

    public ContactDefinitions(BaseTest baseTest) {
        this.baseTest = baseTest;
    }

    @Given("que tengo un body válido para crear un contacto")
    public void prepararContacto() throws IOException {
        body = readBody("createContact.json");
        expectedContact = new HashMap<>(body);
    }


    @Given("que tengo un contacto registrado")
    public void crearContactoParaElEscenario() throws IOException {
        prepararContacto();
        crearContacto();
        baseTest.getResponse().then().log().ifValidationFails().statusCode(201);
        validarDatosDelContacto();
    }

    @When("creo el contacto")
    public void crearContacto() {
        baseTest.setResponse(request().body(JsonOutput.toJson(body)).post(ContactListApi.CONTACTS_PATH));
        if (baseTest.getResponse().statusCode() == 201) {
            baseTest.setContactId(baseTest.getResponse().path("_id"));
        }
    }

    @When("consulto la lista de contactos")
    public void consultarContactos() {
        baseTest.setResponse(request().get(ContactListApi.CONTACTS_PATH));
    }

    @When("consulto el contacto creado")
    public void consultarContacto() {
        baseTest.setResponse(request().get(contactPath()));
    }

    @Given("que tengo un body para actualizar el contacto completo")
    public void prepararActualizacionCompleta() throws IOException {
        body = readBody("updateContact.json");
        expectedContact = new HashMap<>(body);
    }

    @When("actualizo el contacto completo")
    public void actualizarContacto() {
        baseTest.setResponse(request().body(JsonOutput.toJson(body)).put(contactPath()));
    }

    @Given("que tengo un body para actualizar solo el teléfono del contacto")
    public void prepararActualizacionParcial() throws IOException {
        if (expectedContact == null) {
            throw new IllegalStateException("Primero debes crear el contacto que vas a actualizar.");
        }
        body = readBody("partialUpdateContact.json");
        // Los campos que no enviamos deben conservar sus valores.
        expectedContact.putAll(body);
    }

    @When("actualizo parcialmente el contacto")
    public void actualizarParcialmenteContacto() {
        baseTest.setResponse(request().body(JsonOutput.toJson(body)).patch(contactPath()));
    }

    @When("elimino el contacto creado")
    public void eliminarContacto() {
        baseTest.setResponse(request().delete(contactPath()));
        if (baseTest.getResponse().statusCode() == 200) {
            baseTest.getScenario().log("Contacto %s eliminado correctamente.".formatted(baseTest.getContactId()));
            baseTest.setContactId(null);
        }
    }

    @Then("la respuesta de contactos debe tener el status {int}")
    public void validarStatus(int status) {
        baseTest.getResponse().then().log().ifValidationFails().statusCode(status);
    }

    @And("el contacto debe contener los datos enviados")
    public void validarDatosDelContacto() {
        baseTest.getResponse().then()
                .body("_id", not(emptyOrNullString()))
                .body("_id", equalTo(baseTest.getContactId()));
        for (Map.Entry<String, Object> field : expectedContact.entrySet()) {
            baseTest.getResponse().then().body(field.getKey(), equalTo(field.getValue()));
        }
    }

    @And("la lista de contactos debe quedar vacía")
    public void validarListaVacia() {
        baseTest.getResponse().then().body("$", empty());
    }

    private RequestSpecification request() {
        return ContactListApi.authenticatedRequest(baseTest.getToken());
    }

    private String contactPath() {
        if (baseTest.getContactId() == null || baseTest.getContactId().isBlank()) {
            throw new IllegalStateException("Primero debes crear un contacto en este escenario.");
        }
        return "%s/%s".formatted(ContactListApi.CONTACTS_PATH, baseTest.getContactId());
    }

    private Map<String, Object> readBody(String fileName) throws IOException {
        String json = Files.readString(Path.of("src/test/resources/bodies/contactList", fileName));
        return JsonPath.from(json).getMap("$");
    }
}
