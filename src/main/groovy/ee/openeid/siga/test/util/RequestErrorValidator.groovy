package ee.openeid.siga.test.util

import ee.openeid.siga.test.model.RequestError
import io.restassured.response.Response
import org.apache.http.HttpStatus

import static org.hamcrest.Matchers.is

class RequestErrorValidator {
    static validate(Response response, RequestError expectedError) {
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errorCode", is(expectedError.errorCode))
                .body("errorMessage", is(expectedError.errorMessage))
    }
}
