package ee.openeid.siga.test.hashcode.mid

import ee.openeid.siga.test.GenericSpecification
import ee.openeid.siga.test.TestData
import ee.openeid.siga.test.model.Flow
import ee.openeid.siga.test.request.RequestData
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.response.Response
import org.hamcrest.Matchers

@Epic("/hashcodecontainers")
@Feature("Mobile ID signing")
class MobileSigningHashcodeSpec extends GenericSpecification {
    private Flow flow

    def setup() {
        flow = Flow.buildForDefaultTestClientService()
    }

    def "MID signing successful with certificate issued under #certificateCa chain"() {
        given:
        hashcode.uploadContainer(flow, RequestData.uploadHashcodeRequestBody(TestData.DEFAULT_HASHCODE_CONTAINER))

        when:
        Response response = hashcode.startMidSigning(flow,
                RequestData.midSigningRequestBodyMinimal(personId, phoneNo))
        String signatureId = response.path("generatedSignatureId")
        hashcode.pollForMidSigningStatus(flow, signatureId)

        then:
        hashcode.validateContainerInSession(flow)
                .then()
                .body("validationConclusion.validSignaturesCount", Matchers.is(2))

        where:
        certificateCa                         | personId      | phoneNo
        "TEST of SK ID Solutions EID-Q 2021E" | "51307149560" | "+37269930366"
        "TEST of EID-SK 2016"                 | "60001017869" | "+37268000769"
        "TEST of ESTEID-SK 2015"              | "60001019906" | "+37200000766"
    }
}
