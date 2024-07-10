package ee.openeid.siga.test.request


import io.qameta.allure.Step
import io.restassured.http.ContentType
import io.restassured.response.Response

import static io.restassured.RestAssured.given

class Requests {

    @Step("GET {endpoint}")
    static Response get(String endpoint) {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(SigaRequests.sigaServiceUrl + endpoint)
    }
}
