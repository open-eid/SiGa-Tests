package ee.openeid.siga.test.hashcode.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("smartId")
@Epic("Smart-ID signing (hashcode)")
@Feature("SID endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "SID signing request not allowed with invalid profile: #profile"() {
        given: "Create default container"
        hashcode.createDefaultContainer(flow)
        Map startSigningRequestBody = RequestData.sidStartSigningRequestDefaultBody()

        when: "Try signing with invalid profile"
        startSigningRequestBody["signatureProfile"] = profile
        Response response = hashcode.tryStartSidSigning(flow, startSigningRequestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM", "T", "LTA"]
    }

    @Story("SID start certificate choice HTTP method check")
    def "Start SID certificate choice with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        Response certificateChoice = hashcodeRequests.startSidCertificateChoiceRequest(
                flow,
                method,
                RequestData.sidCertificateChoiceRequestDefaultBody())
                .request(method)

        then:
        certificateChoice.then().statusCode(httpStatus)

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

    @Story("SID certificate choice status HTTP method check")
    def "SID certificate choice status with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startSidCertificateChoice(flow, RequestData.sidCertificateChoiceRequestDefaultBody())

        when:
        Response certificateChoiceStatus = hashcodeRequests.getSidCertificateStatusRequest(
                flow,
                method,
                startResponse.path("generatedCertificateId"))
                .request(method)

        then:
        certificateChoiceStatus.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

    @Story("SID start signing HTTP method check")
    def "SID start signing with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        Response startSigningResponse = hashcodeRequests.startSidSigningRequest(
                flow,
                method,
                RequestData.sidStartSigningRequestDefaultBody())
                .request(method)

        then:
        startSigningResponse.then().statusCode(httpStatus)

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

    @Story("SID signing status HTTP method check")
    def "SID signing status with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startSigningResponse = hashcode.startSidSigning(flow, RequestData.sidStartSigningRequestDefaultBody())

        when:
        Response sidSigningStatus = hashcodeRequests.getSidSigningStatusRequest(
                flow,
                method,
                startSigningResponse.path("generatedSignatureId"))
                .request(method)

        then:
        sidSigningStatus.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.GET     || HttpStatus.SC_OK                 | "allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PUT     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

}
