package ee.openeid.siga.test.hashcode.remote

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.helper.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.model.RequestError
import ee.openeid.siga.test.request.RequestData
import ee.openeid.siga.test.util.RequestErrorValidator
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response

@Epic("Hashcode")
@Feature("Remote signing request validation")
class HashcodeRemoteRequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Remote signing not allowed with invalid profile")
    def "Remote signing request not allowed with invalid profile: #profile"() {
        given: "Upload container"
        hashcode.createDefaultContainer(flow)
        Map requestBody = RequestData.remoteSigningStartDefaultRequest()

        when: "Try signing with invalid profile"
        requestBody["signatureProfile"] = profile
        Response response = hashcode.tryStartRemoteSigning(flow, requestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_PROFILE)

        where:
        profile << ["", " ", "123", "@!*", "UNKNOWN", "B_BES", "B_EPES", "LT_TM", "lt_TM", "lt_tm", "LT-TM", "LT TM", "T", "LTA"]
    }

    @Story("Remote signing not allowed with MID/SID certificate")
    def "Hashcode remote signing not allowed with #description certificate policy"() {
        given: "Upload container"
        hashcode.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.remoteSigningStartDefaultRequest()

        when: "Try signing with invalid certificate"
        signingRequestBody["signingCertificate"] = certificatePem
        Response response = hashcode.tryStartRemoteSigning(flow, signingRequestBody)

        then: "Request validation error is returned"
        RequestErrorValidator.validate(response, RequestError.INVALID_CERT)

        where:
        description              | certificatePem
        "Mobile-ID"              | TestData.MID_SELF_SIGNED_SIGNER_CERT_PEM
        "Lithuanian Mobile-ID"   | TestData.MID_LT_SELF_SIGNED_SIGNER_CERT_PEM
        "Smart-ID"               | TestData.SID_SELF_SIGNED_SIGNER_CERT_PEM
        "Smart-ID non qualified" | TestData.SID_NQ_SELF_SIGNED_SIGNER_CERT_PEM
    }

}
