package ee.openeid.siga.test.hashcode.midSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.*
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("mobileId")
@Epic("Mobile-ID signing (hashcode)")
@Feature("MID endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Issue("SIGA-708")
    @Story("MID status not available through SID status endpoint")
    def "MID status polling with SID status polling endpoint not allowed"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midStartSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        Response statusResponse = hashcode.tryGetSmartIdSigningStatus(flow, signatureId)

        then:
        RequestErrorValidator.validate(statusResponse, RequestError.INVALID_TYPE_MID)
    }

    @Story("MID signing HTTP method check")
    def "Start MID signing with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        Response response = hashcodeRequests.startMidSigningRequest(flow, method, RequestData.midStartSigningRequestDefaultBody()).request(method)

        then:
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
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midStartSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        Response response = hashcodeRequests.getMidSigningStatusRequest(flow, method, signatureId).request(method)

        then:
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

    @Story("Get other user MID signing status not allowed")
    def "MID status request for other user container not allowed"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midStartSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        flow.setServiceUuid(Service.SERVICE2.uuid)
        flow.setServiceSecret(Service.SERVICE2.secret)
        Response statusResponse = hashcode.tryGetMidSigningStatus(flow, signatureId)

        then:
        RequestErrorValidator.validate(statusResponse, RequestError.INVALID_RESOURCE)
    }

}
