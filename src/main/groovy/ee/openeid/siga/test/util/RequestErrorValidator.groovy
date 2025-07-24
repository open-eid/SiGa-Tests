package ee.openeid.siga.test.util

import ee.openeid.siga.test.model.RequestError
import io.restassured.response.Response
import org.apache.http.HttpStatus

import static org.hamcrest.Matchers.is

class RequestErrorValidator {
    static validate(Response response, RequestError expectedError) {
        validate(response, expectedError.errorCode, expectedError.errorMessage)
    }

    static validate(Response response, String errorCode, String errorMessage) {
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errorCode", is(errorCode))
                .body("errorMessage", is(errorMessage))
    }
}
