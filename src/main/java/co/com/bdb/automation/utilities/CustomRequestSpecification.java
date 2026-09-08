package co.com.bdb.automation.utilities;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CustomRequestSpecification {

    private final RequestSpecification requestSpecification;

    public CustomRequestSpecification(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Response post() {
        return requestSpecification.post();
    }

    public Response get() {
        return requestSpecification.get();
    }

    public Response put() {
        return requestSpecification.put();
    }

    public Response delete() {
        return requestSpecification.delete();
    }

    public Response head() {
        return requestSpecification.head();
    }

    public Response patch() {
        return requestSpecification.patch();
    }

    public Response options() {
        return requestSpecification.options();
    }

    public CustomRequestSpecification when() {
        return this;
    }
}
