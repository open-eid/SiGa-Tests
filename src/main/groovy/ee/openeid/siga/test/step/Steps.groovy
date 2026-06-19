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

    @Step("Get heartbeat info")
    static Response getHeartbeatInfo() {
        Response response = Requests.get("/actuator/heartbeat")
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

    @Step("Get prometheus info")
    static Response getPrometheusInfo() {
        Response response = Requests.get("/actuator/prometheus")
        response.then().statusCode(HttpStatus.SC_OK)
        return response
    }

}
