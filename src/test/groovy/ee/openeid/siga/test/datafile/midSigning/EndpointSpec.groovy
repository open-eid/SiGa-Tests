package ee.openeid.siga.test.datafile.midSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Tag("mobileId")
@Epic("Mobile-ID signing (datafile)")
@Feature("MID endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("MID signing HTTP method check")
    def "Start MID signing with method #method is #result"() {
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "try starting signing with HTTP method"
        Response response = datafileRequests.startMidSigningRequest(flow, method, RequestData.midSigningRequestDefaultBody()).request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.POST    || HttpStatus.SC_OK                 | "allowed"
        Method.GET     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

    @Story("MID signing status HTTP method check")
    def "Get MID signing status with method #method is #result"() {
        given: "create container and start signing"
        datafile.createDefaultContainer(flow)
        Response startResponse = datafile.startMidSigning(flow, RequestData.midSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when: "try getting signing status with HTTP method"
        Response response = datafileRequests.getMidSigningStatusRequest(flow, method, signatureId).request(method)

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
