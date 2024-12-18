package ee.openeid.siga.test.hashcode.mid

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.model.Service
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.http.Method
import io.restassured.response.Response
import spock.lang.Tag

@Tag("mobileId")
@Epic("Hashcode")
@Feature("MID signing endpoints validation")
class EndpointSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "MID status request for other user container not allowed"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        flow.setServiceUuid(Service.SERVICE2.uuid)
        flow.setServiceSecret(Service.SERVICE2.secret)
        Response statusResponse = hashcode.tryGetMidSigningStatus(flow, signatureId)

        then:
        RequestErrorValidator.validate(statusResponse, RequestError.INVALID_RESOURCE)
    }

    def "MID status endpoint allows HTTP HEAD method"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        Response response = hashcodeRequests.getMidSigningStatusRequest(flow, Method.HEAD, signatureId).head()

        then:
        response.then().statusCode(200)
    }

    @Tag("SIGA-708")
    def "MID status polling with SID status polling endpoint not allowed"() {
        given:
        hashcode.createDefaultContainer(flow)
        Response startResponse = hashcode.startMidSigning(flow, RequestData.midSigningRequestDefaultBody())
        String signatureId = startResponse.path("generatedSignatureId")

        when:
        Response statusResponse = hashcode.tryGetSmartIdSigningStatus(flow, signatureId)

        then:
        RequestErrorValidator.validate(statusResponse, RequestError.INVALID_TYPE_MID)
    }

    def "MID signing request not allowed with invalid profile: #profile"() {
        given: "Upload container"
        hashcode.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.midSigningRequestDefaultBody()

        when: "Try signing with invalid profile"
        signingRequestBody["signatureProfile"] = profile
        Response response = hashcode.tryStartMidSigning(flow, signingRequestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM", "T", "LTA"]
    }
}
