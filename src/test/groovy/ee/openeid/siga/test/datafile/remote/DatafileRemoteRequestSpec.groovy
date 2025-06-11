package ee.openeid.siga.test.datafile.remote

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
import spock.lang.Tag

@Tag("datafileContainer")
@Epic("Datafile")
@Feature("Remote signing request validation")
class DatafileRemoteRequestSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    @Story("Remote signing not allowed with MID/SID certificate")
    def "Datafile remote signing not allowed with #description certificate policy"() {
        given: "Upload container"
        datafile.createDefaultContainer(flow)
        Map signingRequestBody = RequestData.remoteSigningStartDefaultRequest()

        when: "Try signing with invalid certificate"
        signingRequestBody["signingCertificate"] = certificatePem
        Response response = datafile.tryStartRemoteSigning(flow, signingRequestBody)

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
