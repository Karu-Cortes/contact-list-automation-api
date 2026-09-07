package co.com.bdb.automation.utilities;

import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import io.restassured.specification.SpecificationQuerier;

public class CustomRequestSpecification {

    private final RequestSpecification requestSpecification;
    private boolean redirectToLambda = false;

    public CustomRequestSpecification(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public CustomRequestSpecification sendWithLambda() {
        this.redirectToLambda = true;
        return this;
    }

    private Response sendRequestLambda(RequestSpecification requestSpecification, String method) {
        var lambdaContext = new Object() {
            RequestSignerAWS signer;
        };
        QueryableRequestSpecification queryable = SpecificationQuerier.query(requestSpecification);
        lambdaContext.signer = new RequestSignerAWS(
                queryable.getBaseUri(),
                queryable.getBasePath(),
                method,
                queryable.getQueryParams(),
                queryable.getHeaders(),
                queryable.getBody()
        );
        return lambdaContext.signer.performAs();
    }

    public Response post() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "POST");
        }
        return requestSpecification.post();
    }

    public Response get() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "GET");
        }
        return requestSpecification.get();
    }

    public Response put() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "PUT");
        }
        return requestSpecification.put();
    }

    public Response delete() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "DELETE");
        }
        return requestSpecification.delete();
    }

    public Response head() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "HEAD");
        }
        return requestSpecification.head();
    }

    public Response patch() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "PATCH");
        }
        return requestSpecification.patch();
    }

    public Response options() {
        if (redirectToLambda) {
            return sendRequestLambda(requestSpecification, "OPTIONS");
        }
        return requestSpecification.options();
    }

    public CustomRequestSpecification when() {
        return this;
    }
}
