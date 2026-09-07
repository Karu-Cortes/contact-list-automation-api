package co.com.bdb.automation.utilities;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.http.HttpMethodName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.http.Headers;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class RequestSignerAWS {

    static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static String baseUrl;
    private static String path;
    private static String method;
    private static Map<String, String> parameters;
    private static Headers headers;
    private static String body;
    static Response response;

    public RequestSignerAWS(
            String baseUrl,
            String endPoint,
            String method,
            Map<String, String> parameters,
            Headers headers,
            String request) {
        RequestSignerAWS.baseUrl = baseUrl;
        RequestSignerAWS.path = endPoint;
        RequestSignerAWS.method = method;
        RequestSignerAWS.parameters = parameters;
        RequestSignerAWS.headers = headers;
        RequestSignerAWS.body = request;
    }

    public Response performAs() {

        EnvironmentValuesTask env = new EnvironmentValuesTask();
        String endpointLambda = env.getenv("ENDPOINT_LAMBDA");
        String jsonHeaders = gson.toJson(headers.asList().stream().collect(Collectors.toMap(Header::getName, Header::getValue)));
        String jsonParameters = gson.toJson(parameters);
        HashMap<String, String> jsonMapLambda = new HashMap<>();

        if (body == null) {
            jsonMapLambda.put("method", method);
            jsonMapLambda.put("url", baseUrl + path);
            jsonMapLambda.put("params", jsonParameters);
            jsonMapLambda.put("headers", jsonHeaders);
        } else {
            jsonMapLambda.put("method", method);
            jsonMapLambda.put("url", baseUrl + path);
            jsonMapLambda.put("params", jsonParameters);
            jsonMapLambda.put("headers", jsonHeaders);
            jsonMapLambda.put("data", body);
        }

        String jsonLambda =
                gson.toJson(jsonMapLambda).replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}").replace("\\", "");
        Request<Void> request = new DefaultRequest<>("lambda");
        request.setHttpMethod(HttpMethodName.POST);
        request.setEndpoint(URI.create(endpointLambda));
        InputStream str = new ByteArrayInputStream(jsonLambda.getBytes(StandardCharsets.UTF_8));
        request.setContent(str);

        AWSSessionCredentials credentials =
                new AWSSessionCredentials() {
                    @Override
                    public String getSessionToken() {
                        return env.getenv("AWS_SESSION_TOKEN");
                    }

                    @Override
                    public String getAWSAccessKeyId() {
                        return env.getenv("AWS_ACCESS_KEY_ID");
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return env.getenv("AWS_SECRET_ACCESS_KEY");
                    }
                };

        AWS4Signer signer = new AWS4Signer();
        signer.setRegionName("us-east-1");
        signer.setServiceName(request.getServiceName());
        signer.sign(request, credentials);

        request.addHeader("Content-Type", "application/json");

        response = given()
                        .header("Authorization", request.getHeaders().values().toArray()[0].toString())
                        .header("Content-Type", request.getHeaders().values().toArray()[1].toString())
                        .header("Host", request.getHeaders().values().toArray()[2].toString())
                        .header("X-Amz-Date", request.getHeaders().values().toArray()[3].toString())
                        .header("X-Amz-Security-Token", request.getHeaders().values().toArray()[4].toString())
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(jsonLambda)
                        .when()
                        .post(endpointLambda);
        return response;
    }
}
