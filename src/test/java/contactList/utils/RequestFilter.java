package contactList.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

//Validación de request y response TC´s
public class RequestFilter implements Filter {

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext ctx
    ) {
        final var requestInfo = getRequestInfo(requestSpec);
        final var response = ctx.next(requestSpec, responseSpec);
        final var responseInfo = getResponseInfo(response);

        Logs.debug("%s%n%s", requestInfo, responseInfo);

        return response;
    }

    private String getRequestInfo(FilterableRequestSpecification requestSpec) {
        final var requestLine = String.format(
                "%s %s",
                requestSpec.getMethod(),
                requestSpec.getURI()
        );

        final var headers = getHeadersInfo(requestSpec.getHeaders().asList());
        final var body = getRequestBody(requestSpec);

        return String.format("""
                
                =============================================
                Request
                =============================================
                %s
                
                Request Headers:
                %s
                Request Body:
                %s
                """, requestLine, headers, body);
    }

    private String getResponseInfo(Response response) {
        final var responseLine = String.format(
                "%s Response Time: %dms",
                response.getStatusLine(),
                response.getTime()
        );

        final var headers = getHeadersInfo(response.getHeaders().asList());
        final var body = maskSensitiveData(response.getBody().asPrettyString());

        return String.format("""
                =============================================
                Response
                =============================================
                %s
                
                Response Headers:
                %s
                Response Body:
                %s
                """, responseLine, headers, body);
    }

    private String getRequestBody(FilterableRequestSpecification requestSpec) {
        final var body = requestSpec.getBody();

        if (body == null) {
            return "Sin body";
        }

        if (body instanceof File file) {
            try {
                return maskSensitiveData(Files.readString(file.toPath()));
            } catch (IOException e) {
                return "No se pudo leer el archivo de payload: " + e.getMessage();
            }
        }

        final var prettyBody = new Prettifier().getPrettifiedBodyIfPossible(requestSpec);

        if (prettyBody == null || prettyBody.isBlank()) {
            return "Sin body";
        }

        return maskSensitiveData(prettyBody);
    }

    private String getHeadersInfo(List<Header> headers) {
        if (headers == null || headers.isEmpty()) {
            return "Sin headers";
        }

        final var stringBuilder = new StringBuilder();

        for (var header : headers) {
            final var headerName = header.getName();
            final var headerValue = maskHeaderValue(headerName, header.getValue());

            stringBuilder.append(String.format("\t%s: %s%n", headerName, headerValue));
        }

        return stringBuilder.toString();
    }

    private String maskHeaderValue(String name, String value) {
        if (name == null || value == null) {
            return value;
        }

        if ("Authorization".equalsIgnoreCase(name)) {
            return "Bearer ****";
        }

        return value;
    }

    private String maskSensitiveData(String content) {
        if (content == null || content.isBlank()) {
            return "Sin body";
        }

        return content
                .replaceAll("(\"password\"\\s*:\\s*\")[^\"]*(\")", "$1****$2")
                .replaceAll("(\"token\"\\s*:\\s*\")[^\"]*(\")", "$1****$2");
    }
}
