package ee.openeid.siga.test.hashcode.signatures

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus

@Epic("Signatures (hashcode)")
@Feature("Signatures endpoint checks")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Signature list endpoint HTTP method check")
    def "Get signature list with method #method is #result"() {
        given: "upload signed container"
        hashcode.uploadContainerFromFile(flow, TestData.DEFAULT_HASHCODE_CONTAINER_NAME)

        when: "get signature list endpoint request with HTTP method"
        Response response = hashcodeRequests.getSignatureListRequest(flow, method).request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

    @Story("Signature list endpoint request checks")
    def "Get signature list with not existing container ID returns error"() {
        given: "upload signed container"
        hashcode.uploadContainerFromFile(flow, TestData.DEFAULT_HASHCODE_CONTAINER_NAME)

        when: "try getting not existing container signature list"
        flow.setContainerId("not_existing")
        Response response = hashcode.tryGetSignatureList(flow)

        then: "request is not allowed"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

    @Story("Signature list endpoint request checks")
    def "Get signature list of other sessions container returns error"() {
        given: "upload signed container for two services"
        hashcode.uploadContainerFromFile(flow, TestData.DEFAULT_HASHCODE_CONTAINER_NAME)

        Flow flow_service2 = Flow.buildForTestClient1Service2()
        hashcode.uploadContainerFromFile(flow_service2, TestData.DEFAULT_HASHCODE_CONTAINER_NAME)

        when: "try getting signature list of other session container"
        flow.setContainerId(flow_service2.containerId)
        Response response = hashcode.tryGetSignatureList(flow)

        then: "request is not allowed"
        RequestErrorValidator.validate(response, RequestError.INVALID_RESOURCE)
    }

    @Story("Signer info endpoint HTTP method check")
    def "Get signer info with method #method is #result"() {
        given: "upload signed container"
        hashcode.uploadContainerFromFile(flow, TestData.DEFAULT_HASHCODE_CONTAINER_NAME)
        String signatureId = hashcode.getSignatureList(flow).path("signatures[0].generatedSignatureId")

        when: "get signer info endpoint request with HTTP method"
        Response response = hashcodeRequests.getSignatureInfoRequest(flow, method, signatureId).request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

}
