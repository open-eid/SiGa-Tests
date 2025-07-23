package ee.openeid.siga.test.datafile.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Issue
import io.qameta.allure.Story
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus

class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1091")
    @Story("Get signature list endpoint HTTP method check")
    def "Get signature list with method #method is #result"() {
        given: "upload signed container"
        datafile.uploadContainer(flow, RequestData.uploadDatafileRequestBodyFromFile(TestData.DEFAULT_ASICE_CONTAINER_NAME))

        when: "get signature list endpoint request with HTTP method"
        Response response = datafileRequests.getSignatureListRequest(flow, method).request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
//        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }
}
