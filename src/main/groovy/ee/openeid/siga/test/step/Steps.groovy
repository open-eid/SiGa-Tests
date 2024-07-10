package ee.openeid.siga.test.step


import ee.openeid.siga.test.request.Requests
import io.qameta.allure.Step
import io.restassured.response.Response
import org.apache.http.HttpStatus

class Steps {

    @Step("Get version info")
    static Response getVersionInfo() {
        Response response = Requests.get("/actuator/version")
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get health info")
    static Response getHealthInfo() {
        Response response = Requests.get("/actuator/health")
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

}
