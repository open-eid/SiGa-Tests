package ee.openeid.siga.test.datafile.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.Story
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Signatures (datafile)")
@Feature("Signatures endpoint checks")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-1091")
    @Story("Get signature list endpoint HTTP method check")
    def "Get signature list with method #method is #result"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICE_CONTAINER_NAME)

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

    @Story("Get signature list endpoint request checks")
    def "Get signature list with not existing container ID returns error"() {
        given: "upload signed container"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICE_CONTAINER_NAME)

        when: "try getting not existing container signature list"
        flow.setContainerId("not_existing")
        Response response = datafile.tryGetSignatureList(flow)

        then: "request is not allowed"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

    @Story("Get signature list endpoint request checks")
    def "When trying to get signature list of other sessions container, then error is returned"() {
        given: "upload signed container for two services"
        datafile.uploadContainerFromFile(flow, TestData.DEFAULT_ASICS_CONTAINER_NAME)

        Flow flow_service2 = Flow.buildForTestClient1Service2()
        datafile.uploadContainerFromFile(flow_service2, TestData.DEFAULT_ASICE_CONTAINER_NAME)

        when: "try getting signature list of other session container"
        flow.setContainerId(flow_service2.containerId)
        Response response = datafile.tryGetSignatureList(flow)

        then: "request is not allowed"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

}
