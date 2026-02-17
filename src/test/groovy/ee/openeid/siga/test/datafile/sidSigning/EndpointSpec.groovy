package ee.openeid.siga.test.datafile.sidSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Tag("smartId")
@Epic("Smart-ID signing (datafile)")
@Feature("SID endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("SID start certificate choice HTTP method check")
    def "Start SID certificate choice with method #method is #result"() {
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "try starting certificate choice with HTTP method"
        Response certificateChoice = datafileRequests.startSidCertificateChoiceRequest(
                flow,
                method,
                RequestData.sidCertificateChoiceRequestDefaultBody())
                .request(method)

        then: "request is allowed/not allowed"
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
        given: "create container and start certificate choice"
        datafile.createDefaultContainer(flow)
        Response startResponse = datafile.startSidCertificateChoice(flow, RequestData.sidCertificateChoiceRequestDefaultBody())

        when: "try getting certificate choice status with HTTP method"
        Response certificateChoiceStatus = datafileRequests.getSidCertificateStatusRequest(
                flow,
                method,
                startResponse.path("generatedCertificateId"))
                .request(method)

        then: "request is allowed/not allowed"
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
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "try starting signing with HTTP method"
        Response startSigningResponse = datafileRequests.startSidSigningRequest(
                flow,
                method,
                RequestData.sidStartSigningRequestDefaultBody())
                .request(method)

        then: "request is allowed/not allowed"
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
        given: "create container and start signing"
        datafile.createDefaultContainer(flow)
        Response startSigningResponse = datafile.startSidSigning(flow, RequestData.sidStartSigningRequestDefaultBody())

        when: "try getting signing status with HTTP method"
        Response sidSigningStatus = datafileRequests.getSidSigningStatusRequest(
                flow,
                method,
                startSigningResponse.path("generatedSignatureId"))
                .request(method)

        then: "request is allowed/not allowed"
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
