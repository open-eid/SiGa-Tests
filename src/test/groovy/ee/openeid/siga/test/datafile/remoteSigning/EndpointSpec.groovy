package ee.openeid.siga.test.datafile.remoteSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.DigestSigner
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Remote signing (datafile)")
@Feature("Remote signing endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Start remote signing HTTP method check")
    def "Start remote signing with method #method is #result"() {
        given: "create container"
        datafile.createDefaultContainer(flow)

        when: "try starting signing with HTTP method"
        Response response = datafileRequests.startRemoteSigningRequest(
                flow,
                method,
                RequestData.remoteSigningStartDefaultRequest())
                .request(method)

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

    @Story("Finalize remote signing HTTP method check")
    def "Finalize remote signing with method #method is #result"() {
        given: "create container and start signing"
        datafile.createDefaultContainer(flow)
        Response startResponse = datafile.startRemoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when: "try getting signing status with HTTP method"
        Response response = datafileRequests.finalizeRemoteSigningRequest(
                flow,
                method,
                RequestData.remoteSigningFinalizeRequest(DigestSigner.signDigest(startResponse)),
                startResponse.path("generatedSignatureId"))
                .request(method)

        then: "request is allowed/not allowed"
        response.then().statusCode(httpStatus)

        where:
        method         || httpStatus                       | result
        Method.PUT     || HttpStatus.SC_OK                 | "allowed"
        Method.POST    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.GET     || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.DELETE  || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.HEAD    || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.PATCH   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.TRACE   || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
        Method.OPTIONS || HttpStatus.SC_METHOD_NOT_ALLOWED | "not allowed"
    }

}
