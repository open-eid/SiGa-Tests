package ee.openeid.siga.testgroovy

import ee.openeid.siga.test.model.SigaApiFlow
import ee.openeid.siga.testgroovy.helper.TestBaseSpecification
import ee.openeid.siga.webapp.json.CreateHashcodeContainerMobileIdSigningResponse
import ee.openeid.siga.webapp.json.CreateHashcodeContainerSmartIdCertificateChoiceResponse
import ee.openeid.siga.webapp.json.CreateHashcodeContainerSmartIdSigningResponse
import ee.openeid.siga.webapp.json.GetHashcodeContainerSmartIdCertificateChoiceStatusResponse
import io.restassured.response.Response

import static ee.openeid.siga.test.helper.TestData.HASHCODE_CONTAINERS
import static ee.openeid.siga.test.utils.RequestBuilder.*
import static org.hamcrest.CoreMatchers.equalTo

class AuthenticationSpec extends TestBaseSpecification {

    private SigaApiFlow flow

    def setup() {
        flow = SigaApiFlow.buildForTestClient3Service1()
    }

    def "Sign with SID as Client 3 - no contact info"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())
        Response certificateChoice = postSidCertificateChoice(flow, smartIdCertificateChoiceRequest("30303039914", "EE"))
        String generatedCertificateId = certificateChoice.as(CreateHashcodeContainerSmartIdCertificateChoiceResponse.class).getGeneratedCertificateId()

        pollForSidCertificateStatus(flow, generatedCertificateId)

        String documentNumber = flow.getSidCertificateStatus().as(GetHashcodeContainerSmartIdCertificateChoiceStatusResponse.class).getDocumentNumber()
        Response signingResponse = postSmartIdSigningInSession(flow, smartIdSigningRequestWithDefault("LT", documentNumber))
        String generatedSignatureId = signingResponse.as(CreateHashcodeContainerSmartIdSigningResponse.class).getGeneratedSignatureId()
        pollForSidSigning(flow, generatedSignatureId)

        Response validationResponse = getValidationReportForContainerInSession(flow)

        validationResponse.then()
                .statusCode(200)
                .body("validationConclusion.validSignaturesCount", equalTo(1))
    }

    def "Sign with MID as Client 3 - no contact info"() {
        expect:
        postCreateContainer(flow, hashcodeContainersDataRequestWithDefault())
        Response response = postMidSigningInSession(flow, midSigningRequestWithDefault("60001019906", "+37200000766", "LT"))
        String signatureId = response.as(CreateHashcodeContainerMobileIdSigningResponse.class).getGeneratedSignatureId()
        pollForMidSigning(flow, signatureId)

        Response validationResponse = getValidationReportForContainerInSession(flow)

        validationResponse.then()
                .statusCode(200)
                .body("validationConclusion.validSignaturesCount", equalTo(1))
    }

    @Override
    String getContainerEndpoint() {
        HASHCODE_CONTAINERS
    }
}
