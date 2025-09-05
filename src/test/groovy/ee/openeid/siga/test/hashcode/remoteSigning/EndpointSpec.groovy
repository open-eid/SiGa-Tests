package ee.openeid.siga.test.hashcode.remoteSigning

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.DigestSigner
import io.qameta.allure.*
import io.restassured.http.Method
import io.restassured.response.Response
import org.apache.http.HttpStatus

@Epic("Remote signing (hashcode)")
@Feature("Remote signing endpoint validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Start remote signing HTTP method check")
    def "Start remote signing with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)

        when:
        Response response = hashcodeRequests.startRemoteSigningRequest(
                flow,
                method,
                RequestData.remoteSigningStartDefaultRequest())
                .request(method)

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

    @Story("Finalize remote signing HTTP method check")
    def "Finalize remote signing with method #method is #result"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startRemoteSigning(flow, RequestData.remoteSigningStartDefaultRequest())

        when:
        Response response = hashcodeRequests.finalizeRemoteSigningRequest(
                flow,
                method,
                RequestData.remoteSigningFinalizeRequest(DigestSigner.signDigest(startResponse)),
                startResponse.path("generatedSignatureId"))
                .request(method)

        then:
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
